package com.homeflex.core.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;

import java.time.Duration;

/**
 * Configures Resilience4j circuit breakers and retry policies for external services.
 * <p>
 * Circuit breakers trip after consecutive failures and prevent cascading outages.
 * Retry policies handle transient network errors with exponential backoff.
 */
@Configuration
public class ResilienceConfig {

    // ── Circuit Breaker Registry ───────────────────────────────────────

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    /**
     * Email service circuit breaker: trips after 5 consecutive SMTP failures.
     * Half-open after 30 seconds, allows 3 probe calls to test recovery.
     */
    @Bean
    public CircuitBreaker emailCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .failureRateThreshold(100)   // 5/5 = 100% → trip
                .minimumNumberOfCalls(5)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .recordExceptions(MailException.class, Exception.class)
                .build();

        return registry.circuitBreaker("emailService", config);
    }

    /**
     * Firebase push notification circuit breaker: trips after 5 consecutive failures.
     * Half-open after 60 seconds to allow Firebase to recover.
     */
    @Bean
    public CircuitBreaker firebaseCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .failureRateThreshold(100)
                .minimumNumberOfCalls(5)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        return registry.circuitBreaker("firebaseNotifications", config);
    }

    // ── Retry Registry ─────────────────────────────────────────────────

    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }

    /**
     * Stripe API retry: 3 attempts with exponential backoff (500ms, 1s, 2s).
     * Only retries on transient network/server errors, not on validation errors.
     */
    @Bean
    public Retry stripeRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(500, 2.0))
                .retryExceptions(
                        com.stripe.exception.ApiConnectionException.class,
                        com.stripe.exception.RateLimitException.class,
                        com.stripe.exception.ApiException.class
                )
                .ignoreExceptions(
                        com.stripe.exception.InvalidRequestException.class,
                        com.stripe.exception.AuthenticationException.class,
                        com.stripe.exception.CardException.class
                )
                .build();

        return registry.retry("stripeApi", config);
    }
}
