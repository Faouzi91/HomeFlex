package com.homeflex.core.security;

import com.homeflex.core.domain.entity.Permission;
import com.homeflex.core.domain.entity.Role;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.domain.repository.RoleRepository;
import com.homeflex.core.domain.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${app.jwt.cookie.access-token-name:ACCESS_TOKEN}")
    private String accessTokenCookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromCookie(request);

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                String userId = tokenProvider.getUserIdFromToken(jwt);

                User user = userRepository.findById(UUID.fromString(userId))
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (!user.getIsActive()) {
                    throw new RuntimeException("User is not active");
                }

                List<GrantedAuthority> authorities = buildAuthorities(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> buildAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        // Fallback: user has no RBAC rows (migration not yet run, or broken state).
        // Look up the role by the legacy enum and load its full permission set so
        // @PreAuthorize("hasAuthority(BOOKING_CREATE)") still works.
        if (authorities.isEmpty() && user.getRole() != null) {
            String roleName = "ROLE_" + user.getRole().name();
            authorities.add(new SimpleGrantedAuthority(roleName));
            roleRepository.findByName(roleName).ifPresent(role -> {
                for (Permission perm : role.getPermissions()) {
                    authorities.add(new SimpleGrantedAuthority(perm.getName()));
                }
            });
            logger.warn("User " + user.getId() + " has no user_roles rows — using legacy role fallback. " +
                "Run V28 migration or check DataInitializer logs.");
        }

        return authorities;
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (accessTokenCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
