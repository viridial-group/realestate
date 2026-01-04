package com.realestate.common.client;

import com.realestate.common.client.dto.OrganizationInfoDTO;
import com.realestate.common.client.dto.PermissionCheckDTO;
import com.realestate.common.client.dto.UserInfoDTO;
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
import java.util.List;
import java.util.Optional;

/**
 * Client for communicating with Identity Service
 * Provides methods to check permissions, get user info, etc.
 */
@Component
public class IdentityServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(IdentityServiceClient.class);
    private static final String CIRCUIT_BREAKER_NAME = "identityService";
    private static final String RETRY_NAME = "identityService";

    private final WebClient webClient;
    private final String baseUrl;

    public IdentityServiceClient(
            @Value("${services.identity.url:http://localhost:8081}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }

    /**
     * Get user information by ID
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getUserByIdFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Optional<UserInfoDTO>> getUserById(Long userId, String authToken) {
        return webClient.get()
                .uri("/api/identity/users/{id}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .retrieve()
                .bodyToMono(UserInfoDTO.class)
                .map(Optional::of)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        logger.warn("User not found: {}", userId);
                        return Mono.just(Optional.empty());
                    }
                    logger.error("Error fetching user {}: {}", userId, ex.getMessage());
                    return Mono.just(Optional.empty());
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error fetching user {}: {}", userId, ex.getMessage());
                    return Mono.just(Optional.empty());
                });
    }

    /**
     * Get user information by email
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getUserByEmailFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Optional<UserInfoDTO>> getUserByEmail(String email, String authToken) {
        // Note: This assumes Identity Service has an endpoint /api/identity/users/email/{email}
        // If not, we'll need to add it or use a different approach
        return webClient.get()
                .uri("/api/identity/users/email/{email}", email)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .retrieve()
                .bodyToMono(UserInfoDTO.class)
                .map(Optional::of)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        logger.warn("User not found: {}", email);
                        return Mono.just(Optional.empty());
                    }
                    logger.error("Error fetching user by email {}: {}", email, ex.getMessage());
                    return Mono.just(Optional.empty());
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error fetching user by email {}: {}", email, ex.getMessage());
                    return Mono.just(Optional.empty());
                });
    }

    /**
     * Check if user has a specific permission
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "checkPermissionFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Boolean> checkPermission(Long userId, String permission, String resourceType, Long resourceId, String authToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/identity/permissions/check")
                        .queryParam("userId", userId)
                        .queryParam("permission", permission)
                        .queryParam("resourceType", resourceType)
                        .queryParam("resourceId", resourceId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .retrieve()
                .bodyToMono(PermissionCheckDTO.class)
                .map(PermissionCheckDTO::getHasPermission)
                .defaultIfEmpty(false)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(ex -> {
                    logger.error("Error checking permission for user {}: {}", userId, ex.getMessage());
                    return Mono.just(false); // Fail closed - deny access on error
                });
    }

    // Fallback methods
    private Mono<Optional<UserInfoDTO>> getUserByIdFallback(Long userId, String authToken, Exception ex) {
        logger.error("Circuit breaker opened for getUserById. User: {}", userId, ex);
        return Mono.just(Optional.empty());
    }

    private Mono<Optional<UserInfoDTO>> getUserByEmailFallback(String email, String authToken, Exception ex) {
        logger.error("Circuit breaker opened for getUserByEmail. Email: {}", email, ex);
        return Mono.just(Optional.empty());
    }

    /**
     * Get organization information by ID
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getOrganizationByIdFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Optional<OrganizationInfoDTO>> getOrganizationById(Long organizationId, String authToken) {
        return webClient.get()
                .uri("/api/identity/organizations/{id}", organizationId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .retrieve()
                .bodyToMono(OrganizationInfoDTO.class)
                .map(Optional::of)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        logger.warn("Organization not found: {}", organizationId);
                        return Mono.just(Optional.empty());
                    }
                    logger.error("Error fetching organization {}: {}", organizationId, ex.getMessage());
                    return Mono.just(Optional.empty());
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error fetching organization {}: {}", organizationId, ex.getMessage());
                    return Mono.just(Optional.empty());
                });
    }

    private Mono<Boolean> checkPermissionFallback(Long userId, String permission, String resourceType, Long resourceId, String authToken, Exception ex) {
        logger.error("Circuit breaker opened for checkPermission. User: {}, Permission: {}", userId, permission, ex);
        return Mono.just(false); // Fail closed - deny access
    }

    private Mono<Optional<OrganizationInfoDTO>> getOrganizationByIdFallback(Long organizationId, String authToken, Exception ex) {
        logger.error("Circuit breaker opened for getOrganizationById. Organization: {}", organizationId, ex);
        return Mono.just(Optional.empty());
    }

    /**
     * Get all active organizations (public endpoint, no auth required)
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getAllOrganizationsFallback")
    @Retry(name = RETRY_NAME)
    public Mono<List<OrganizationInfoDTO>> getAllOrganizations() {
        return webClient.get()
                .uri("/api/public/organizations")
                .retrieve()
                .bodyToFlux(OrganizationInfoDTO.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(ex -> {
                    logger.error("Error fetching all organizations: {}", ex.getMessage());
                    return Mono.just(new java.util.ArrayList<>());
                });
    }

    /**
     * Get organization by ID (public endpoint, no auth required)
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getOrganizationByIdPublicFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Optional<OrganizationInfoDTO>> getOrganizationByIdPublic(Long organizationId) {
        return webClient.get()
                .uri("/api/public/organizations/{id}", organizationId)
                .retrieve()
                .bodyToMono(OrganizationInfoDTO.class)
                .map(Optional::of)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        logger.warn("Organization not found: {}", organizationId);
                        return Mono.just(Optional.empty());
                    }
                    logger.error("Error fetching organization {}: {}", organizationId, ex.getMessage());
                    return Mono.just(Optional.empty());
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error fetching organization {}: {}", organizationId, ex.getMessage());
                    return Mono.just(Optional.empty());
                });
    }

    private Mono<List<OrganizationInfoDTO>> getAllOrganizationsFallback(Exception ex) {
        logger.error("Circuit breaker opened for getAllOrganizations", ex);
        return Mono.just(new java.util.ArrayList<>());
    }

    private Mono<Optional<OrganizationInfoDTO>> getOrganizationByIdPublicFallback(Long organizationId, Exception ex) {
        logger.error("Circuit breaker opened for getOrganizationByIdPublic. Organization: {}", organizationId, ex);
        return Mono.just(Optional.empty());
    }
}

