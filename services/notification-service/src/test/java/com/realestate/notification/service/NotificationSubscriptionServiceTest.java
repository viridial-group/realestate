package com.realestate.notification.service;

import com.realestate.notification.entity.NotificationSubscription;
import com.realestate.notification.repository.NotificationSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSubscriptionServiceTest {

    @Mock
    private NotificationSubscriptionRepository subscriptionRepository;

    @InjectMocks
    private NotificationSubscriptionService subscriptionService;

    private NotificationSubscription testSubscription;

    @BeforeEach
    void setUp() {
        testSubscription = new NotificationSubscription();
        testSubscription.setId(1L);
        testSubscription.setUserId(10L);
        testSubscription.setOrganizationId(100L);
        testSubscription.setNotificationType("PROPERTY_CREATED");
        testSubscription.setChannel("IN_APP");
        testSubscription.setEnabled(true);
        testSubscription.setActive(true);
    }

    @Test
    void testCreateSubscription_Success() {
        // Given
        when(subscriptionRepository.save(any(NotificationSubscription.class))).thenReturn(testSubscription);

        // When
        NotificationSubscription result = subscriptionService.createSubscription(testSubscription);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getNotificationType(), result.getNotificationType());
        verify(subscriptionRepository).save(testSubscription);
    }

    @Test
    void testGetSubscriptionById_Success() {
        // Given
        Long id = 1L;
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(testSubscription));

        // When
        Optional<NotificationSubscription> result = subscriptionService.getSubscriptionById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSubscription.getId(), result.get().getId());
        verify(subscriptionRepository).findById(id);
    }

    @Test
    void testGetSubscriptionsByUserId_Success() {
        // Given
        Long userId = 10L;
        List<NotificationSubscription> subscriptions = Arrays.asList(testSubscription);
        when(subscriptionRepository.findActiveEnabledByUserId(userId)).thenReturn(subscriptions);

        // When
        List<NotificationSubscription> result = subscriptionService.getSubscriptionsByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subscriptionRepository).findActiveEnabledByUserId(userId);
    }

    @Test
    void testGetSubscriptionByUserIdAndType_Success() {
        // Given
        Long userId = 10L;
        String notificationType = "PROPERTY_CREATED";
        when(subscriptionRepository.findActiveByUserIdAndType(userId, notificationType))
                .thenReturn(Optional.of(testSubscription));

        // When
        Optional<NotificationSubscription> result = subscriptionService.getSubscriptionByUserIdAndType(userId, notificationType);

        // Then
        assertTrue(result.isPresent());
        assertEquals(notificationType, result.get().getNotificationType());
        verify(subscriptionRepository).findActiveByUserIdAndType(userId, notificationType);
    }

    @Test
    void testUpdateSubscription_Success() {
        // Given
        Long id = 1L;
        NotificationSubscription updateDetails = new NotificationSubscription();
        updateDetails.setChannel("PUSH");
        updateDetails.setEnabled(false);

        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(testSubscription));
        when(subscriptionRepository.save(any(NotificationSubscription.class))).thenReturn(testSubscription);

        // When
        NotificationSubscription result = subscriptionService.updateSubscription(id, updateDetails);

        // Then
        assertNotNull(result);
        assertEquals("PUSH", result.getChannel());
        assertFalse(result.getEnabled());
        verify(subscriptionRepository).findById(id);
        verify(subscriptionRepository).save(testSubscription);
    }

    @Test
    void testDeleteSubscription_Success() {
        // Given
        Long id = 1L;
        when(subscriptionRepository.existsById(id)).thenReturn(true);
        doNothing().when(subscriptionRepository).deleteById(id);

        // When
        assertDoesNotThrow(() -> subscriptionService.deleteSubscription(id));

        // Then
        verify(subscriptionRepository).existsById(id);
        verify(subscriptionRepository).deleteById(id);
    }
}

