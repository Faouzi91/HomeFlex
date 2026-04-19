package com.homeflex.core.config;

import com.homeflex.core.security.HomeFlexPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

/**
 * Wires the custom PermissionEvaluator into Spring's method-security expression
 * handler. Kept separate from SecurityConfig to avoid a circular bean dependency
 * (SecurityConfig → UserRepository → ... vs PermissionEvaluator → Repositories).
 */
@Configuration
public class MethodSecurityConfig {

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            HomeFlexPermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }
}
