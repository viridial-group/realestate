package com.realestate.common.client;

import com.realestate.common.client.dto.DomainInfoDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * Client for communicating with Resource Service
 * Provides methods to check domains, tags, etc.
 */
@Component
public class ResourceServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceClient.class);
    private static final String CIRCUIT_BREAKER_NAME = "resourceService";
    private static final String RETRY_NAME = "resourceService";

    private final WebClient webClient;
    private final String baseUrl;

    public ResourceServiceClient(
            @Value("${services.resource.url:http://localhost:8084}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }

    /**
     * Get domain information by ID
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getDomainByIdFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Optional<DomainInfoDTO>> getDomainById(Long domainId, String authToken) {
        return webClient.get()
                .uri("/api/resources/domains/{id}", domainId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .retrieve()
                .bodyToMono(DomainInfoDTO.class)
                .map(Optional::of)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        logger.warn("Domain not found: {}", domainId);
                        return Mono.just(Optional.empty());
                    }
                    logger.error("Error fetching domain {}: {}", domainId, ex.getMessage());
                    return Mono.just(Optional.empty());
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error fetching domain {}: {}", domainId, ex.getMessage());
                    return Mono.just(Optional.empty());
                });
    }

    /**
     * Check if domain exists and is active
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "validateDomainFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Boolean> validateDomain(Long domainId, Long organizationId, String authToken) {
        return getDomainById(domainId, authToken)
                .map(domainOpt -> domainOpt
                        .map(domain -> domain.getActive() != null && domain.getActive()
                                && (domain.getOrganizationId() == null || domain.getOrganizationId().equals(organizationId)))
                        .orElse(false))
                .defaultIfEmpty(false)
                .onErrorResume(ex -> {
                    logger.error("Error validating domain {}: {}", domainId, ex.getMessage());
                    return Mono.just(false);
                });
    }

    // Fallback methods
    private Mono<Optional<DomainInfoDTO>> getDomainByIdFallback(Long domainId, String authToken, Exception ex) {
        logger.error("Circuit breaker opened for getDomainById. Domain: {}", domainId, ex);
        return Mono.just(Optional.empty());
    }

    private Mono<Boolean> validateDomainFallback(Long domainId, Long organizationId, String authToken, Exception ex) {
        logger.error("Circuit breaker opened for validateDomain. Domain: {}", domainId, ex);
        return Mono.just(false); // Fail closed - deny validation
    }
}

