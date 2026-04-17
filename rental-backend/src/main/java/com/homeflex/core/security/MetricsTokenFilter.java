package com.homeflex.core.security;

import com.homeflex.core.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

/**
 * Authenticates Prometheus scrape requests against a static Bearer token.
 * Only applies to /actuator/** paths. On match, grants ROLE_MONITORING so
 * SecurityConfig can authorize the request.
 */
@Component
@RequiredArgsConstructor
public class MetricsTokenFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String expectedToken = appProperties.getMonitoring().getMetricsToken();

        if (authHeader != null && authHeader.startsWith("Bearer ")
                && expectedToken != null && !expectedToken.isBlank()) {
            String token = authHeader.substring(7);
            if (MessageDigest.isEqual(token.getBytes(), expectedToken.getBytes())) {
                var auth = new UsernamePasswordAuthenticationToken(
                        "prometheus", null,
                        List.of(new SimpleGrantedAuthority("ROLE_MONITORING")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
