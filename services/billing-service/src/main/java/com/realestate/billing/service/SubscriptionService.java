package com.realestate.billing.service;

import com.realestate.billing.entity.Plan;
import com.realestate.billing.entity.Subscription;
import com.realestate.billing.repository.PlanRepository;
import com.realestate.billing.repository.SubscriptionRepository;
import com.realestate.common.exception.BadRequestException;
import com.realestate.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
    }

    @Transactional
    public Subscription createSubscription(Long planId, Long organizationId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId));

        // Vérifier qu'il n'y a pas déjà une subscription active
        subscriptionRepository.findByOrganizationId(organizationId)
                .ifPresent(existing -> {
                    if (existing.getActive() && "ACTIVE".equals(existing.getStatus())) {
                        throw new BadRequestException("Organization already has an active subscription");
                    }
                });

        Subscription subscription = new Subscription(plan, organizationId);
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(LocalDateTime.now());

        // Calculer la date de fin selon la période de facturation
        if ("MONTHLY".equals(plan.getBillingPeriod())) {
            subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        } else if ("YEARLY".equals(plan.getBillingPeriod())) {
            subscription.setEndDate(LocalDateTime.now().plusYears(1));
        }

        return subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Subscription> getSubscriptionByOrganizationId(Long organizationId) {
        return subscriptionRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public Optional<Subscription> getActiveSubscriptionByOrganizationId(Long organizationId) {
        return subscriptionRepository.findActiveByOrganizationId(organizationId);
    }

    @Transactional
    public Subscription updateSubscription(Long id, Subscription subscriptionDetails) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", id));

        if (subscriptionDetails.getStatus() != null) {
            subscription.setStatus(subscriptionDetails.getStatus());
        }
        if (subscriptionDetails.getEndDate() != null) {
            subscription.setEndDate(subscriptionDetails.getEndDate());
        }
        if (subscriptionDetails.getAutoRenew() != null) {
            subscription.setAutoRenew(subscriptionDetails.getAutoRenew());
        }
        if (subscriptionDetails.getActive() != null) {
            subscription.setActive(subscriptionDetails.getActive());
        }

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription cancelSubscription(Long id, Long cancelledBy) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", id));

        subscription.setStatus("CANCELLED");
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancelledBy(cancelledBy);
        subscription.setActive(false);
        subscription.setAutoRenew(false);

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription renewSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", id));

        Plan plan = subscription.getPlan();
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(LocalDateTime.now());

        // Calculer la nouvelle date de fin
        if ("MONTHLY".equals(plan.getBillingPeriod())) {
            subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        } else if ("YEARLY".equals(plan.getBillingPeriod())) {
            subscription.setEndDate(LocalDateTime.now().plusYears(1));
        }

        return subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getExpiringSubscriptions(LocalDateTime date) {
        return subscriptionRepository.findExpiringSubscriptions(date);
    }
}

