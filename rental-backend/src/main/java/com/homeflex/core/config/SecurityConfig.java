package com.homeflex.core.config;

import com.homeflex.core.security.JwtAuthenticationFilter;
import com.homeflex.core.security.MetricsTokenFilter;
import com.homeflex.core.security.RateLimitFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import com.homeflex.core.domain.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import java.io.IOException;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final MetricsTokenFilter metricsTokenFilter;
    private final RateLimitFilter rateLimitFilter;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepo)
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                        .ignoringRequestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/webhooks/**",
                                "/ws/**"
                        )
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; connect-src 'self' https: wss: ws:; font-src 'self' https: data:; frame-ancestors 'none'; object-src 'none'; base-uri 'self'; form-action 'self'"
                        ))
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .contentTypeOptions(contentType -> {})
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/webhooks/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/prometheus").hasAnyRole("ADMIN", "MONITORING")
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/stats").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/config").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/currencies/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/my-properties")
                                .hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/*/reports").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/cities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/*/similar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/properties/*/view").permitAll()

                        // Vehicle public endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/*/view").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/*/availability").permitAll()
                        // Vehicle bookings list contains PII — owner or admin only.
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/*/bookings")
                                .hasAnyRole("LANDLORD", "ADMIN")

                        // Vehicle owner endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/my-vehicles")
                                .hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/vehicles/**").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vehicles/**").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/*/images").hasAnyRole("LANDLORD", "ADMIN")

                        // Vehicle admin endpoints
                        .requestMatchers("/api/v1/vehicles/admin/**").hasRole("ADMIN")

                        // KYC endpoints
                        .requestMatchers("/api/v1/kyc/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/kyc/**").authenticated()

                        // Insurance endpoints
                        .requestMatchers("/api/v1/insurance/**").authenticated()

                        // Dispute endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/disputes").authenticated()
                        .requestMatchers("/api/v1/disputes/**").hasRole("ADMIN")

                        // GDPR endpoints
                        .requestMatchers("/api/v1/gdpr/**").authenticated()

                        // Payment & payout endpoints
                        .requestMatchers("/api/v1/payments/refund").hasRole("ADMIN")
                        .requestMatchers("/api/v1/payments/**").authenticated()
                        .requestMatchers("/api/v1/payouts/**").hasAnyRole("LANDLORD", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/property/**").permitAll()

                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/properties").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/properties/json").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/properties/**").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/properties/**").hasAnyRole("LANDLORD", "ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // Prevent CsrfAuthenticationStrategy from deleting the XSRF-TOKEN cookie
                        // on every JWT-authenticated request (stateless apps don't rotate CSRF per request)
                        .sessionAuthenticationStrategy((auth, req, res) -> {})
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(metricsTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, JwtAuthenticationFilter.class)
                .addFilterAfter(new CsrfCookieFilter(csrfRepo), RateLimitFilter.class);

        return http.build();
    }

    /**
     * Registers CsrfDeletionGuard BEFORE Spring Security's FilterChainProxy so that
     * CsrfFilter itself captures the guarded wrapper — making all saveToken() calls
     * (including the deferred first-visit write) route through the guard.
     * This guarantees the XSRF-TOKEN deletion emitted by CsrfAuthenticationStrategy
     * is silently dropped at every call site in the chain.
     */
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> csrfDeletionGuardRegistration() {
        FilterRegistrationBean<OncePerRequestFilter> reg = new FilterRegistrationBean<>(
                new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest req,
                                                    HttpServletResponse resp,
                                                    FilterChain chain) throws ServletException, IOException {
                        chain.doFilter(req, new CsrfDeletionGuard(resp));
                    }
                });
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        var corsConfig = appProperties.getCors();
        configuration.setAllowedOrigins(corsConfig.getAllowedOrigins());
        configuration.setAllowedMethods(corsConfig.getAllowedMethods());
        configuration.setAllowedHeaders(corsConfig.getAllowedHeaders());
        configuration.setExposedHeaders(corsConfig.getExposedHeaders());
        configuration.setAllowCredentials(corsConfig.isAllowCredentials());
        configuration.setMaxAge(corsConfig.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPasswordHash() != null ? user.getPasswordHash() : "")
                    .roles(user.getRole().name())
                    .accountLocked(!user.getIsActive())
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // ── SPA CSRF support (from Spring Security reference docs) ──────────

    /**
     * Resolves the CSRF token value for single-page applications.
     * When the token arrives in a header (Angular's X-XSRF-TOKEN), uses the plain
     * handler so it can be compared directly against the cookie value.
     * When submitted via a form parameter, delegates to the XOR-encoded handler
     * for BREACH protection.
     */
    static final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
        private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           Supplier<CsrfToken> csrfToken) {
            this.delegate.handle(request, response, csrfToken);
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
                return super.resolveCsrfTokenValue(request, csrfToken);
            }
            return this.delegate.resolveCsrfTokenValue(request, csrfToken);
        }
    }

    /**
     * Forces the deferred CSRF token BEFORE the chain executes so that the
     * Set-Cookie header is written into the response buffer before Spring MVC's
     * message converters flush/commit the response.
     *
     * CsrfDeletionGuard (the outer servlet filter) blocks the XSRF-TOKEN deletion
     * that CsrfAuthenticationStrategy emits later in the chain (SessionManagementFilter),
     * so the cookie written here survives to the client.
     */
    static final class CsrfCookieFilter extends OncePerRequestFilter {
        private final CookieCsrfTokenRepository tokenRepository;

        CsrfCookieFilter(CookieCsrfTokenRepository tokenRepository) {
            this.tokenRepository = tokenRepository;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            // Force deferred token evaluation NOW — before Spring MVC commits the response.
            // If the client has no cookie yet, this writes Set-Cookie: XSRF-TOKEN=<uuid>.
            // If it already has one, saveToken() is skipped and the existing cookie persists.
            CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (token != null) {
                token.getToken();
            }
            filterChain.doFilter(request, response);
        }
    }

    /** Intercepts addCookie to silently drop XSRF-TOKEN deletion calls. */
    static final class CsrfDeletionGuard extends HttpServletResponseWrapper {
        CsrfDeletionGuard(HttpServletResponse response) { super(response); }

        @Override
        public void addCookie(Cookie cookie) {
            if ("XSRF-TOKEN".equals(cookie.getName())
                    && (cookie.getMaxAge() == 0 || cookie.getValue() == null || cookie.getValue().isEmpty())) {
                return; // drop the deletion — client keeps its existing token
            }
            super.addCookie(cookie);
        }
    }
}
