package com.realestate.common.client;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Client for communicating with Emailing Service
 * Provides methods to send emails using templates
 */
@Component
public class EmailServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceClient.class);
    private static final String CIRCUIT_BREAKER_NAME = "emailService";
    private static final String RETRY_NAME = "emailService";

    private final WebClient webClient;
    private final String baseUrl;

    public EmailServiceClient(
            @Value("${services.emailing.url:http://localhost:8088}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }

    /**
     * Send email from template
     * 
     * @param templateName Name of the template
     * @param recipientEmail Email of the recipient
     * @param recipientId ID of the recipient (optional)
     * @param organizationId Organization ID
     * @param variables Variables for template replacement
     * @param authToken Authorization token (optional)
     * @return Mono<Boolean> true if email was sent successfully
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "sendEmailFromTemplateFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Boolean> sendEmailFromTemplate(
            String templateName,
            String recipientEmail,
            Long recipientId,
            Long organizationId,
            Map<String, Object> variables,
            String authToken) {
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("templateName", templateName);
        requestBody.put("recipientEmail", recipientEmail);
        if (recipientId != null) {
            requestBody.put("recipientId", recipientId);
        }
        requestBody.put("organizationId", organizationId);
        if (variables != null) {
            requestBody.put("variables", variables);
        } else {
            requestBody.put("variables", new HashMap<>());
        }

        return webClient.post()
                .uri("/api/emails/template")
                .header(HttpHeaders.AUTHORIZATION, authToken != null ? "Bearer " + authToken : "")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> true)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Error sending email from template {} to {}: {} - {}", 
                            templateName, recipientEmail, ex.getStatusCode(), ex.getMessage());
                    return Mono.just(false);
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error sending email from template {} to {}: {}", 
                            templateName, recipientEmail, ex.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Send email from template (async, fire and forget)
     * This method doesn't wait for the response and doesn't block
     */
    public void sendEmailFromTemplateAsync(
            String templateName,
            String recipientEmail,
            Long recipientId,
            Long organizationId,
            Map<String, Object> variables,
            String authToken) {
        sendEmailFromTemplate(templateName, recipientEmail, recipientId, organizationId, variables, authToken)
                .subscribe(
                        success -> {
                            if (success) {
                                logger.debug("Email sent successfully from template {} to {}", templateName, recipientEmail);
                            } else {
                                logger.warn("Failed to send email from template {} to {}", templateName, recipientEmail);
                            }
                        },
                        error -> logger.error("Error in async email sending to {}: {}", recipientEmail, error.getMessage())
                );
    }

    /**
     * Send a simple email (without template)
     * 
     * @param recipientEmail Email of the recipient
     * @param subject Email subject
     * @param body Email body (HTML)
     * @param organizationId Organization ID
     * @param authToken Authorization token (optional)
     * @return Mono<Boolean> true if email was sent successfully
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "sendEmailFallback")
    @Retry(name = RETRY_NAME)
    public Mono<Boolean> sendEmail(
            String recipientEmail,
            String subject,
            String body,
            Long organizationId,
            String authToken) {
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("recipientEmail", recipientEmail);
        requestBody.put("subject", subject);
        requestBody.put("body", body);
        requestBody.put("organizationId", organizationId);

        return webClient.post()
                .uri("/api/emails")
                .header(HttpHeaders.AUTHORIZATION, authToken != null ? "Bearer " + authToken : "")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> true)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Error sending email to {}: {} - {}", 
                            recipientEmail, ex.getStatusCode(), ex.getMessage());
                    return Mono.just(false);
                })
                .onErrorResume(ex -> {
                    logger.error("Unexpected error sending email to {}: {}", 
                            recipientEmail, ex.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Send a simple email (async, fire and forget)
     */
    public void sendEmailAsync(
            String recipientEmail,
            String subject,
            String body,
            Long organizationId,
            String authToken) {
        sendEmail(recipientEmail, subject, body, organizationId, authToken)
                .subscribe(
                        success -> {
                            if (success) {
                                logger.debug("Email sent successfully to {}", recipientEmail);
                            } else {
                                logger.warn("Failed to send email to {}", recipientEmail);
                            }
                        },
                        error -> logger.error("Error in async email sending to {}: {}", recipientEmail, error.getMessage())
                );
    }

    // Fallback methods
    private Mono<Boolean> sendEmailFromTemplateFallback(
            String templateName,
            String recipientEmail,
            Long recipientId,
            Long organizationId,
            Map<String, Object> variables,
            String authToken,
            Exception ex) {
        logger.error("Circuit breaker opened for sendEmailFromTemplate. Template: {}, Recipient: {}", 
                templateName, recipientEmail, ex);
        return Mono.just(false);
    }

    private Mono<Boolean> sendEmailFallback(
            String recipientEmail,
            String subject,
            String body,
            Long organizationId,
            String authToken,
            Exception ex) {
        logger.error("Circuit breaker opened for sendEmail. Recipient: {}", recipientEmail, ex);
        return Mono.just(false);
    }
}

