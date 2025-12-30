package com.realestate.emailing.service;

import com.realestate.emailing.entity.Email;
import com.realestate.emailing.entity.EmailTemplate;
import com.realestate.emailing.repository.EmailRepository;
import com.realestate.emailing.repository.EmailTemplateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final EmailTemplateRepository templateRepository;
    private final JavaMailSender mailSender;
    private final String defaultFromEmail;

    public EmailService(
            EmailRepository emailRepository,
            EmailTemplateRepository templateRepository,
            JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.templateRepository = templateRepository;
        this.mailSender = mailSender;
        this.defaultFromEmail = "support@viridial.com";
    }

    @Transactional
    public Email sendEmail(Email email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getRecipientEmail());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true); // true = HTML
            helper.setFrom(email.getSenderEmail() != null ? email.getSenderEmail() : defaultFromEmail);

            mailSender.send(message);

            email.setStatus("SENT");
            email.setSentAt(LocalDateTime.now());
            email.setRetryCount(0);

            return emailRepository.save(email);
        } catch (MessagingException e) {
            email.setStatus("FAILED");
            email.setFailedAt(LocalDateTime.now());
            email.setErrorMessage(e.getMessage());
            email.setRetryCount(email.getRetryCount() + 1);
            return emailRepository.save(email);
        }
    }

    @Transactional
    public Email sendEmailFromTemplate(
            String templateName,
            String recipientEmail,
            Long recipientId,
            Long organizationId,
            Map<String, Object> variables) {
        Optional<EmailTemplate> templateOpt = templateRepository
                .findActiveByNameAndOrganization(templateName, organizationId);

        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template not found: " + templateName);
        }

        EmailTemplate template = templateOpt.get();

        // Remplacer les variables dans le sujet et le corps
        String subject = replaceVariables(template.getSubject(), variables);
        String body = processTemplate(template.getBody(), variables);

        Email email = new Email(recipientEmail, subject, body, organizationId);
        email.setTemplate(template);
        email.setRecipientId(recipientId);
        email.setSenderEmail(defaultFromEmail);

        return sendEmail(email);
    }

    @Transactional
    public Email createEmail(Email email) {
        return emailRepository.save(email);
    }

    @Transactional(readOnly = true)
    public Optional<Email> getEmailById(Long id) {
        return emailRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Email> getEmailsByRecipientEmail(String recipientEmail) {
        return emailRepository.findByRecipientEmail(recipientEmail);
    }

    @Transactional(readOnly = true)
    public List<Email> getEmailsByOrganizationId(Long organizationId) {
        return emailRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public List<Email> getPendingEmailsForRetry(Integer maxRetries) {
        return emailRepository.findPendingEmailsForRetry(maxRetries);
    }

    @Transactional
    public Email retryFailedEmail(Long id) {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found with id: " + id));

        if (email.getRetryCount() >= 3) {
            throw new RuntimeException("Max retry count reached for email: " + id);
        }

        return sendEmail(email);
    }

    private String replaceVariables(String text, Map<String, Object> variables) {
        String result = text;
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
            }
        }
        return result;
    }

    private String processTemplate(String templateBody, Map<String, Object> variables) {
        // Simple variable replacement (Thymeleaf can be added later if needed)
        return replaceVariables(templateBody, variables);
    }
}

