package com.realestate.emailing.service;

import com.realestate.emailing.entity.Email;
import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.repository.EmailRepository;
import com.realestate.emailing.repository.EmailTemplateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private EmailTemplateRepository templateRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private Email testEmail;
    private EmailTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testEmail = new Email();
        testEmail.setId(1L);
        testEmail.setRecipientEmail("test@example.com");
        testEmail.setSubject("Test Subject");
        testEmail.setBody("Test Body");
        testEmail.setOrganizationId(100L);
        testEmail.setStatus("PENDING");

        testTemplate = new EmailTemplate();
        testTemplate.setId(1L);
        testTemplate.setName("welcome_email");
        testTemplate.setType("WELCOME");
        testTemplate.setSubject("Welcome {{name}}");
        testTemplate.setBody("Hello {{name}}, welcome to our platform!");
        testTemplate.setOrganizationId(100L);
        testTemplate.setActive(true);
    }

    @Test
    void testSendEmail_Success() throws MessagingException {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailRepository.save(any(Email.class))).thenAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            email.setId(1L);
            return email;
        });

        // When
        Email result = emailService.sendEmail(testEmail);

        // Then
        assertNotNull(result);
        assertEquals("SENT", result.getStatus());
        assertNotNull(result.getSentAt());
        verify(mailSender).send(any(MimeMessage.class));
        verify(emailRepository).save(any(Email.class));
    }

    @Test
    void testSendEmail_Failure() throws MessagingException {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doAnswer(invocation -> {
            throw new MessagingException("SMTP error");
        }).when(mailSender).send(any(MimeMessage.class));
        when(emailRepository.save(any(Email.class))).thenAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            email.setId(1L);
            return email;
        });

        // When
        Email result = emailService.sendEmail(testEmail);

        // Then
        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
        assertNotNull(result.getFailedAt());
        assertEquals(1, result.getRetryCount());
        verify(emailRepository).save(any(Email.class));
    }

    @Test
    void testSendEmailFromTemplate_Success() throws MessagingException {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        Map<String, Object> variables = Map.of("name", "John");
        when(templateRepository.findActiveByNameAndOrganization("welcome_email", 100L))
                .thenReturn(Optional.of(testTemplate));
        when(emailRepository.save(any(Email.class))).thenAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            email.setId(1L);
            email.setStatus("SENT");
            return email;
        });

        // When
        Email result = emailService.sendEmailFromTemplate(
                "welcome_email", "test@example.com", 10L, 100L, variables);

        // Then
        assertNotNull(result);
        verify(templateRepository).findActiveByNameAndOrganization("welcome_email", 100L);
        verify(emailRepository, atLeastOnce()).save(any(Email.class));
    }

    @Test
    void testGetEmailById_Success() {
        // Given
        Long id = 1L;
        when(emailRepository.findById(id)).thenReturn(Optional.of(testEmail));

        // When
        Optional<Email> result = emailService.getEmailById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEmail.getId(), result.get().getId());
        verify(emailRepository).findById(id);
    }

    @Test
    void testGetEmailsByRecipientEmail_Success() {
        // Given
        String recipientEmail = "test@example.com";
        List<Email> emails = Arrays.asList(testEmail);
        when(emailRepository.findByRecipientEmail(recipientEmail)).thenReturn(emails);

        // When
        List<Email> result = emailService.getEmailsByRecipientEmail(recipientEmail);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(emailRepository).findByRecipientEmail(recipientEmail);
    }

    @Test
    void testRetryFailedEmail_Success() throws MessagingException {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        Long id = 1L;
        testEmail.setStatus("FAILED");
        testEmail.setRetryCount(1);
        when(emailRepository.findById(id)).thenReturn(Optional.of(testEmail));
        when(emailRepository.save(any(Email.class))).thenAnswer(invocation -> {
            Email email = invocation.getArgument(0);
            email.setId(1L);
            return email;
        });

        // When
        Email result = emailService.retryFailedEmail(id);

        // Then
        assertNotNull(result);
        verify(emailRepository).findById(id);
        verify(emailRepository, atLeastOnce()).save(any(Email.class));
    }

    @Test
    void testRetryFailedEmail_MaxRetriesReached() {
        // Given
        Long id = 1L;
        testEmail.setStatus("FAILED");
        testEmail.setRetryCount(3);
        when(emailRepository.findById(id)).thenReturn(Optional.of(testEmail));

        // When & Then
        assertThrows(RuntimeException.class, () -> emailService.retryFailedEmail(id));
        verify(emailRepository).findById(id);
        verify(emailRepository, never()).save(any(Email.class));
    }
}

