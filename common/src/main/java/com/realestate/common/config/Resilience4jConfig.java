package com.realestate.common.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerProperties;
import io.github.resilience4j.springboot3.retry.autoconfigure.RetryProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for Resilience4j (Circuit Breaker and Retry)
 */
@Configuration
@ConditionalOnProperty(name = "resilience4j.enabled", havingValue = "true", matchIfMissing = true)
public class Resilience4jConfig {

    /**
     * Circuit Breaker configuration for inter-service calls
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Open circuit after 50% failure rate
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait 30s before trying again
                .slidingWindowSize(10) // Last 10 calls
                .minimumNumberOfCalls(5) // Need at least 5 calls before calculating failure rate
                .permittedNumberOfCallsInHalfOpenState(3) // Allow 3 calls in half-open state
                .build();
    }

    /**
     * Retry configuration for inter-service calls
     */
    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3) // Retry up to 3 times
                .waitDuration(Duration.ofMillis(500)) // Wait 500ms between retries
                .retryOnException(throwable -> {
                    // Retry on network errors, timeouts, and 5xx errors
                    return throwable instanceof java.net.ConnectException
                            || throwable instanceof java.util.concurrent.TimeoutException
                            || (throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException
                            && ((org.springframework.web.reactive.function.client.WebClientResponseException) throwable)
                            .getStatusCode().is5xxServerError());
                })
                .build();
    }
}

