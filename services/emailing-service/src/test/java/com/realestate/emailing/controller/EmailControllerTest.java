package com.realestate.emailing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.emailing.entity.Email;
import com.realestate.emailing.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmailController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private Email createTestEmail() {
        Email email = new Email();
        email.setId(1L);
        email.setRecipientEmail("test@example.com");
        email.setSubject("Test Subject");
        email.setBody("Test Body");
        email.setOrganizationId(100L);
        email.setStatus("SENT");
        return email;
    }

    @Test
    void testSendEmail_Success() throws Exception {
        // Given
        Email email = createTestEmail();
        when(emailService.sendEmail(any(Email.class))).thenReturn(email);

        // When & Then
        mockMvc.perform(post("/api/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void testSendEmailFromTemplate_Success() throws Exception {
        // Given
        Email email = createTestEmail();
        when(emailService.sendEmailFromTemplate(anyString(), anyString(), any(), anyLong(), any())).thenReturn(email);

        // When & Then
        mockMvc.perform(post("/api/emails/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"templateName\":\"welcome_email\",\"recipientEmail\":\"test@example.com\",\"organizationId\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void testGetEmailById_Success() throws Exception {
        // Given
        Long id = 1L;
        Email email = createTestEmail();
        when(emailService.getEmailById(id)).thenReturn(Optional.of(email));

        // When & Then
        mockMvc.perform(get("/api/emails/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.recipientEmail").value("test@example.com"));
    }

    @Test
    void testGetEmails_Success() throws Exception {
        // Given
        String recipientEmail = "test@example.com";
        List<Email> emails = Arrays.asList(createTestEmail());
        when(emailService.getEmailsByRecipientEmail(recipientEmail)).thenReturn(emails);

        // When & Then
        mockMvc.perform(get("/api/emails")
                        .param("recipientEmail", recipientEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testRetryEmail_Success() throws Exception {
        // Given
        Long id = 1L;
        Email email = createTestEmail();
        when(emailService.retryFailedEmail(id)).thenReturn(email);

        // When & Then
        mockMvc.perform(post("/api/emails/{id}/retry", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"));
    }
}

