package com.realestate.rental.config;

import com.realestate.rental.security.JwtAuthenticationFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.realestate.rental.domain.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

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
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/webhooks/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/stats").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/my-properties")
                                .hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/*/reports").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/*/similar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/properties/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/properties/*/view").permitAll()

                        // Vehicle public endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/*/view").permitAll()

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

                        // Payment endpoints
                        .requestMatchers("/api/v1/payments/refund").hasRole("ADMIN")
                        .requestMatchers("/api/v1/payments/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/property/**").permitAll()

                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/properties").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/properties/json").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/properties/**").hasAnyRole("LANDLORD", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/properties/**").hasAnyRole("LANDLORD", "ADMIN")

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
}