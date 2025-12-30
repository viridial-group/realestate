package com.realestate.notification.service;

import com.realestate.notification.entity.NotificationSubscription;
import com.realestate.notification.repository.NotificationSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationSubscriptionService {

    private final NotificationSubscriptionRepository subscriptionRepository;

    public NotificationSubscriptionService(NotificationSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public NotificationSubscription createSubscription(NotificationSubscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationSubscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<NotificationSubscription> getSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findActiveEnabledByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationSubscription> getSubscriptionByUserIdAndType(Long userId, String notificationType) {
        return subscriptionRepository.findActiveByUserIdAndType(userId, notificationType);
    }

    @Transactional
    public NotificationSubscription updateSubscription(Long id, NotificationSubscription subscriptionDetails) {
        NotificationSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        if (subscriptionDetails.getChannel() != null) {
            subscription.setChannel(subscriptionDetails.getChannel());
        }
        if (subscriptionDetails.getEnabled() != null) {
            subscription.setEnabled(subscriptionDetails.getEnabled());
        }
        if (subscriptionDetails.getActive() != null) {
            subscription.setActive(subscriptionDetails.getActive());
        }

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new RuntimeException("Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }
}

