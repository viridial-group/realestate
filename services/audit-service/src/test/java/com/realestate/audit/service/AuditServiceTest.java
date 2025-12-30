package com.realestate.audit.service;

import com.realestate.audit.entity.AuditLog;
import com.realestate.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    private AuditLog testAuditLog;

    @BeforeEach
    void setUp() {
        testAuditLog = new AuditLog();
        testAuditLog.setId(1L);
        testAuditLog.setActorId(10L);
        testAuditLog.setActorEmail("user@example.com");
        testAuditLog.setOrganizationId(100L);
        testAuditLog.setAction("CREATE");
        testAuditLog.setTargetType("PROPERTY");
        testAuditLog.setTargetId(50L);
        testAuditLog.setStatus("SUCCESS");
        testAuditLog.setDescription("Property created");
        testAuditLog.setIpAddress("192.168.1.1");
        testAuditLog.setUserAgent("Mozilla/5.0");
        testAuditLog.setRequestMethod("POST");
        testAuditLog.setRequestPath("/api/properties");
    }

    @Test
    void testCreateAuditLog_Success() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        AuditLog result = auditService.createAuditLog(testAuditLog);

        // Then
        assertNotNull(result);
        assertEquals(testAuditLog.getAction(), result.getAction());
        verify(auditLogRepository).save(testAuditLog);
    }

    @Test
    void testLogAction_Success() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        AuditLog result = auditService.logAction(
                10L, "user@example.com", 100L, "CREATE", "PROPERTY", 50L,
                "SUCCESS", "Property created", "192.168.1.1", "Mozilla/5.0",
                "POST", "/api/properties", null);

        // Then
        assertNotNull(result);
        assertEquals("CREATE", result.getAction());
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void testGetAuditLogById_Success() {
        // Given
        Long id = 1L;
        when(auditLogRepository.findById(id)).thenReturn(Optional.of(testAuditLog));

        // When
        Optional<AuditLog> result = auditService.getAuditLogById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testAuditLog.getId(), result.get().getId());
        verify(auditLogRepository).findById(id);
    }

    @Test
    void testGetAuditLogsByOrganizationId_Success() {
        // Given
        Long organizationId = 100L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(testAuditLog));
        when(auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable))
                .thenReturn(page);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByOrganizationId(organizationId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(auditLogRepository).findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);
    }

    @Test
    void testGetAuditLogsByTarget_Success() {
        // Given
        String targetType = "PROPERTY";
        Long targetId = 50L;
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        when(auditLogRepository.findByTargetOrderByCreatedAtDesc(targetType, targetId))
                .thenReturn(auditLogs);

        // When
        List<AuditLog> result = auditService.getAuditLogsByTarget(targetType, targetId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository).findByTargetOrderByCreatedAtDesc(targetType, targetId);
    }

    @Test
    void testGetAuditLogsByDateRange_Success() {
        // Given
        Long organizationId = 100L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(testAuditLog));
        when(auditLogRepository.findByOrganizationIdAndDateRange(organizationId, startDate, endDate, pageable))
                .thenReturn(page);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByDateRange(organizationId, startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(auditLogRepository).findByOrganizationIdAndDateRange(organizationId, startDate, endDate, pageable);
    }
}

