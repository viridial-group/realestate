package com.realestate.notification.controller;

import com.realestate.notification.entity.NotificationSubscription;
import com.realestate.notification.service.NotificationSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications/subscriptions")
@Tag(name = "Notification Subscriptions", description = "Notification subscription management API for managing user notification preferences")
public class NotificationSubscriptionController {

    private final NotificationSubscriptionService subscriptionService;

    public NotificationSubscriptionController(NotificationSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @Operation(summary = "Create subscription", description = "Creates a new notification subscription for a user")
    public ResponseEntity<NotificationSubscription> createSubscription(@Valid @RequestBody NotificationSubscription subscription) {
        NotificationSubscription created = subscriptionService.createSubscription(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID", description = "Returns subscription information for a specific subscription ID")
    public ResponseEntity<NotificationSubscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List subscriptions", description = "Returns a list of subscriptions for a user")
    public ResponseEntity<List<NotificationSubscription>> getSubscriptions(@RequestParam Long userId) {
        List<NotificationSubscription> subscriptions = subscriptionService.getSubscriptionsByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription", description = "Updates subscription information for a specific subscription ID")
    public ResponseEntity<NotificationSubscription> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody NotificationSubscription subscriptionDetails) {
        try {
            NotificationSubscription updated = subscriptionService.updateSubscription(id, subscriptionDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscription", description = "Deletes a subscription from the database by ID")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        try {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

