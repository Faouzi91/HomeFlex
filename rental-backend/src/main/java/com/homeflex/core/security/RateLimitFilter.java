package com.homeflex.core.security;

import com.homeflex.core.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Fixed-window rate limiter backed by Redis atomic increments.
 * <p>
 * A single Lua script executes {@code INCR} + conditional {@code EXPIRE}
 * in one round-trip, guaranteeing atomicity even under high concurrency.
 *
 * <h3>Limits</h3>
 * <ul>
 *   <li><strong>Authenticated</strong> — keyed by user ID,
 *       default 100 requests / 60 s</li>
 *   <li><strong>Anonymous</strong> — keyed by client IP,
 *       default 20 requests / 60 s</li>
 * </ul>
 *
 * <h3>Response headers</h3>
 * Every response includes {@code X-RateLimit-Limit} and
 * {@code X-RateLimit-Remaining}.  When the limit is exceeded the
 * filter short-circuits with {@code 429 Too Many Requests} and a
 * {@code Retry-After} header (seconds until the window resets).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private final AppProperties appProperties;

    /**
     * Lua script — atomic increment with lazy TTL.
     * <p>
     * Returns the counter value <em>after</em> the increment so the
     * caller can compare it against the limit.  The {@code EXPIRE} is
     * set only on the first request in a window (when {@code INCR}
     * returns 1) to avoid resetting the TTL on subsequent requests.
     */
    private static final RedisScript<Long> INCREMENT_SCRIPT = RedisScript.of(
            """
            local key   = KEYS[1]
            local limit = tonumber(ARGV[1])
            local window = tonumber(ARGV[2])
            local current = redis.call('INCR', key)
            if current == 1 then
                redis.call('EXPIRE', key, window)
            end
            return current
            """,
            Long.class
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        AppProperties.RateLimit cfg = appProperties.getRateLimit();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());

        String key = buildKey(request, auth, authenticated);
        int limit = authenticated
                ? cfg.getAuthenticatedRequestsPerMinute()
                : cfg.getPublicRequestsPerMinute();
        int windowSeconds = cfg.getWindowSeconds();

        Long current = redisTemplate.execute(
                INCREMENT_SCRIPT,
                List.of(key),
                String.valueOf(limit),
                String.valueOf(windowSeconds)
        );

        if (current == null) {
            // Redis unavailable — fail open so we don't block traffic
            log.warn("Redis rate-limit script returned null; allowing request");
            filterChain.doFilter(request, response);
            return;
        }

        long remaining = Math.max(0, limit - current);
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

        if (current > limit) {
            Long ttl = redisTemplate.getExpire(key);
            long retryAfter = (ttl != null && ttl > 0) ? ttl : windowSeconds;
            response.setHeader("Retry-After", String.valueOf(retryAfter));

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"error\":\"Too many requests. Please try again in "
                            + retryAfter + " seconds.\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String buildKey(HttpServletRequest request,
                            Authentication auth,
                            boolean authenticated) {
        String identifier = authenticated
                ? auth.getName()
                : resolveClientIp(request);
        return "rate_limit:" + identifier;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // Use the LAST entry set by our trusted reverse proxy (Nginx),
            // not the first which can be spoofed by the client.
            String[] parts = xff.split(",");
            return parts[parts.length - 1].trim();
        }
        return request.getRemoteAddr();
    }
}
