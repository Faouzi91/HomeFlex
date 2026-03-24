package com.realestate.rental.config;

import com.realestate.rental.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.realestate.rental.repository.UserRepository;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserRepository userRepository;
    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;
    @Value("${app.cors.allowed-methods}")
    private List<String> allowedMethods;
    @Value("${app.cors.allowed-headers}")
    private List<String> allowedHeaders;
    @Value("${app.cors.exposed-headers}")
    private List<String> exposedHeaders;
    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;
    @Value("${app.cors.max-age}")
    private long maxAge;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
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
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/stats").permitAll() // Allow Landing page stats
                        .requestMatchers(HttpMethod.GET, "/api/properties/*/similar").permitAll() // Allow Similar properties
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/*/similar").permitAll()

                        // Public property search
                        .requestMatchers(HttpMethod.GET, "/api/properties/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/properties/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/properties/{id}/view").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/properties/{id}/view").permitAll()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Landlord endpoints
                        .requestMatchers(HttpMethod.POST, "/api/properties").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAnyRole("LANDLORD", "ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(exposedHeaders);
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

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
}