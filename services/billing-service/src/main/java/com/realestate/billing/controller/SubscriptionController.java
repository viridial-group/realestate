package com.realestate.billing.controller;

import com.realestate.billing.dto.SubscriptionDTO;
import com.realestate.billing.entity.Subscription;
import com.realestate.billing.mapper.SubscriptionMapper;
import com.realestate.billing.service.SubscriptionService;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.common.client.IdentityServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription management API for managing organization subscriptions to plans")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper subscriptionMapper;
    private final IdentityServiceClient identityServiceClient;

    public SubscriptionController(
            SubscriptionService subscriptionService, 
            SubscriptionMapper subscriptionMapper,
            IdentityServiceClient identityServiceClient) {
        this.subscriptionService = subscriptionService;
        this.subscriptionMapper = subscriptionMapper;
        this.identityServiceClient = identityServiceClient;
    }

    @GetMapping
    @Operation(summary = "List all subscriptions", description = "Returns a list of all subscriptions with optional status filter. Automatically filters by user permissions and accessible organizations.")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String status) {
        try {
            // Récupérer le contexte de permissions si un token est fourni
            Set<Long> accessibleOrgIds = null;
            boolean isSuperAdmin = false;
            boolean isAdmin = false;
            
            if (authorization != null && authorization.startsWith("Bearer ") && identityServiceClient != null) {
                String token = authorization.substring(7);
                
                try {
                    java.util.Optional<com.realestate.common.client.dto.PermissionContextDTO> permissionContextOpt = 
                            identityServiceClient.getPermissionContext(token).block();
                    
                    if (permissionContextOpt.isPresent()) {
                        com.realestate.common.client.dto.PermissionContextDTO permissionContext = permissionContextOpt.get();
                        isSuperAdmin = permissionContext.isSuperAdmin();
                        isAdmin = permissionContext.isAdmin();
                        accessibleOrgIds = permissionContext.getAccessibleOrganizationIds();
                    }
                } catch (Exception e) {
                    // Continuer sans contexte de permissions
                }
            }
            
            List<Subscription> subscriptions;
            
            // Si l'utilisateur n'est pas super admin/admin, filtrer selon ses permissions
            if (!isSuperAdmin && !isAdmin && accessibleOrgIds != null && !accessibleOrgIds.isEmpty()) {
                // Utiliser le filtrage avec permissions
                subscriptions = subscriptionService.getSubscriptionsWithPermissions(accessibleOrgIds, status);
            } else {
                // Super admin ou admin : voir tous les abonnements
                if (status != null && !status.isEmpty()) {
                    subscriptions = subscriptionService.getSubscriptionsByStatus(status);
                } else {
                    subscriptions = subscriptionService.getAllSubscriptions();
                }
            }
            
            List<SubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                    .map(subscriptionMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(subscriptionDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "Create subscription", description = "Creates a new subscription for an organization to a plan")
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody Map<String, Long> request) {
        Long planId = request.get("planId");
        Long organizationId = request.get("organizationId");
        Subscription created = subscriptionService.createSubscription(planId, organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID", description = "Returns subscription information for a specific subscription ID")
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable Long id) {
        Subscription subscription = subscriptionService.getSubscriptionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", id));
        return ResponseEntity.ok(subscriptionMapper.toDTO(subscription));
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get subscription by organization", description = "Returns the subscription for a specific organization")
    public ResponseEntity<SubscriptionDTO> getSubscriptionByOrganizationId(@PathVariable Long organizationId) {
        Subscription subscription = subscriptionService.getSubscriptionByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", organizationId));
        return ResponseEntity.ok(subscriptionMapper.toDTO(subscription));
    }

    @GetMapping("/organization/{organizationId}/active")
    @Operation(summary = "Get active subscription by organization", description = "Returns the active subscription for a specific organization")
    public ResponseEntity<SubscriptionDTO> getActiveSubscriptionByOrganizationId(@PathVariable Long organizationId) {
        Subscription subscription = subscriptionService.getActiveSubscriptionByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Active subscription", organizationId));
        return ResponseEntity.ok(subscriptionMapper.toDTO(subscription));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription", description = "Updates an existing subscription")
    public ResponseEntity<SubscriptionDTO> updateSubscription(@PathVariable Long id, @Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        Subscription subscription = new Subscription();
        subscription.setStatus(subscriptionDTO.getStatus());
        subscription.setEndDate(subscriptionDTO.getEndDate());
        subscription.setAutoRenew(subscriptionDTO.getAutoRenew());
        subscription.setActive(subscriptionDTO.getActive());
        Subscription updated = subscriptionService.updateSubscription(id, subscription);
        return ResponseEntity.ok(subscriptionMapper.toDTO(updated));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancels an existing subscription")
    public ResponseEntity<SubscriptionDTO> cancelSubscription(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long cancelledBy = request.get("cancelledBy");
        Subscription cancelled = subscriptionService.cancelSubscription(id, cancelledBy);
        return ResponseEntity.ok(subscriptionMapper.toDTO(cancelled));
    }

    @PostMapping("/{id}/renew")
    @Operation(summary = "Renew subscription", description = "Renews an existing subscription")
    public ResponseEntity<SubscriptionDTO> renewSubscription(@PathVariable Long id) {
        Subscription renewed = subscriptionService.renewSubscription(id);
        return ResponseEntity.ok(subscriptionMapper.toDTO(renewed));
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get expiring subscriptions", description = "Returns subscriptions expiring before a specific date")
    public ResponseEntity<List<SubscriptionDTO>> getExpiringSubscriptions(@RequestParam(required = false) LocalDateTime date) {
        LocalDateTime checkDate = date != null ? date : LocalDateTime.now().plusDays(7);
        List<Subscription> subscriptions = subscriptionService.getExpiringSubscriptions(checkDate);
        List<SubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                .map(subscriptionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriptionDTOs);
    }
}

