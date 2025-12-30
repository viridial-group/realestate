package com.realestate.notification.service;

import com.realestate.notification.entity.Notification;
import com.realestate.notification.entity.NotificationSubscription;
import com.realestate.notification.repository.NotificationRepository;
import com.realestate.notification.repository.NotificationSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSubscriptionRepository subscriptionRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationSubscriptionRepository subscriptionRepository) {
        this.notificationRepository = notificationRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        // Vérifier si l'utilisateur est abonné à ce type de notification
        Optional<NotificationSubscription> subscription = subscriptionRepository
                .findActiveByUserIdAndType(notification.getRecipientId(), notification.getType());

        // Si l'utilisateur n'est pas abonné ou a désactivé les notifications, on peut quand même créer la notification
        // mais elle sera marquée comme inactive si l'abonnement n'existe pas ou est désactivé
        if (subscription.isEmpty() || !subscription.get().getEnabled()) {
            // On crée quand même la notification mais elle sera peut-être filtrée plus tard
        }

        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification sendNotification(
            String type,
            String title,
            String message,
            Long recipientId,
            Long organizationId,
            Long senderId,
            String channel,
            String targetType,
            Long targetId) {
        Notification notification = new Notification(type, title, message, recipientId, organizationId);
        notification.setSenderId(senderId);
        notification.setChannel(channel);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setStatus("SENT");

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByRecipientId(Long recipientId) {
        return notificationRepository.findActiveByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByRecipientId(Long recipientId) {
        return notificationRepository.findUnreadByRecipientId(recipientId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCountByRecipientId(Long recipientId) {
        return notificationRepository.countUnreadByRecipientId(recipientId);
    }

    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        if (notification.getStatus().equals("PENDING")) {
            notification.setStatus("READ");
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }

        return notification;
    }

    @Transactional
    public Notification markAsArchived(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.setStatus("ARCHIVED");
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification updateNotification(Long id, Notification notificationDetails) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        if (notificationDetails.getTitle() != null) {
            notification.setTitle(notificationDetails.getTitle());
        }
        if (notificationDetails.getMessage() != null) {
            notification.setMessage(notificationDetails.getMessage());
        }
        if (notificationDetails.getStatus() != null) {
            notification.setStatus(notificationDetails.getStatus());
        }
        if (notificationDetails.getActive() != null) {
            notification.setActive(notificationDetails.getActive());
        }

        return notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }
}

