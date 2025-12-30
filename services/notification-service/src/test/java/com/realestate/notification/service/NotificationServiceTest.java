package com.realestate.notification.service;

import com.realestate.notification.entity.Notification;
import com.realestate.notification.entity.NotificationSubscription;
import com.realestate.notification.repository.NotificationRepository;
import com.realestate.notification.repository.NotificationSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationSubscriptionRepository subscriptionRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setType("INFO");
        testNotification.setTitle("New Property Created");
        testNotification.setMessage("A new property has been created");
        testNotification.setRecipientId(10L);
        testNotification.setOrganizationId(100L);
        testNotification.setSenderId(5L);
        testNotification.setStatus("PENDING");
        testNotification.setChannel("IN_APP");
        testNotification.setTargetType("PROPERTY");
        testNotification.setTargetId(50L);
        testNotification.setActive(true);
    }

    @Test
    void testCreateNotification_Success() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(subscriptionRepository.findActiveByUserIdAndType(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // When
        Notification result = notificationService.createNotification(testNotification);

        // Then
        assertNotNull(result);
        assertEquals(testNotification.getTitle(), result.getTitle());
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void testSendNotification_Success() {
        // Given
        Notification sentNotification = new Notification();
        sentNotification.setId(1L);
        sentNotification.setStatus("SENT");
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notif = invocation.getArgument(0);
            notif.setId(1L);
            return notif;
        });

        // When
        Notification result = notificationService.sendNotification(
                "INFO", "Title", "Message", 10L, 100L, 5L, "IN_APP", "PROPERTY", 50L);

        // Then
        assertNotNull(result);
        assertEquals("SENT", result.getStatus());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void testGetNotificationById_Success() {
        // Given
        Long id = 1L;
        when(notificationRepository.findById(id)).thenReturn(Optional.of(testNotification));

        // When
        Optional<Notification> result = notificationService.getNotificationById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testNotification.getId(), result.get().getId());
        verify(notificationRepository).findById(id);
    }

    @Test
    void testGetNotificationsByRecipientId_Success() {
        // Given
        Long recipientId = 10L;
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findActiveByRecipientIdOrderByCreatedAtDesc(recipientId))
                .thenReturn(notifications);

        // When
        List<Notification> result = notificationService.getNotificationsByRecipientId(recipientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository).findActiveByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Test
    void testGetUnreadNotificationsByRecipientId_Success() {
        // Given
        Long recipientId = 10L;
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findUnreadByRecipientId(recipientId)).thenReturn(notifications);

        // When
        List<Notification> result = notificationService.getUnreadNotificationsByRecipientId(recipientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository).findUnreadByRecipientId(recipientId);
    }

    @Test
    void testGetUnreadCountByRecipientId_Success() {
        // Given
        Long recipientId = 10L;
        when(notificationRepository.countUnreadByRecipientId(recipientId)).thenReturn(5L);

        // When
        Long result = notificationService.getUnreadCountByRecipientId(recipientId);

        // Then
        assertEquals(5L, result);
        verify(notificationRepository).countUnreadByRecipientId(recipientId);
    }

    @Test
    void testMarkAsRead_Success() {
        // Given
        Long id = 1L;
        when(notificationRepository.findById(id)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        Notification result = notificationService.markAsRead(id);

        // Then
        assertNotNull(result);
        assertEquals("READ", result.getStatus());
        assertNotNull(result.getReadAt());
        verify(notificationRepository).findById(id);
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void testMarkAsArchived_Success() {
        // Given
        Long id = 1L;
        when(notificationRepository.findById(id)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        Notification result = notificationService.markAsArchived(id);

        // Then
        assertNotNull(result);
        assertEquals("ARCHIVED", result.getStatus());
        verify(notificationRepository).findById(id);
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void testUpdateNotification_Success() {
        // Given
        Long id = 1L;
        Notification updateDetails = new Notification();
        updateDetails.setTitle("Updated Title");
        updateDetails.setStatus("READ");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        Notification result = notificationService.updateNotification(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("READ", result.getStatus());
        verify(notificationRepository).findById(id);
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void testDeleteNotification_Success() {
        // Given
        Long id = 1L;
        when(notificationRepository.existsById(id)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> notificationService.deleteNotification(id));

        // Then
        verify(notificationRepository).existsById(id);
        verify(notificationRepository).deleteById(id);
    }
}

