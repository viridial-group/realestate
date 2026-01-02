package com.realestate.billing.service;

import com.realestate.billing.entity.Invoice;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Service pour générer des PDFs de factures
 * Note: Cette implémentation utilise une approche simple avec HTML/CSS
 * Pour une production réelle, utilisez une bibliothèque comme iText, Apache PDFBox, ou OpenPDF
 */
@Service
public class InvoicePdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Génère un PDF simple pour une facture
     * TODO: Implémenter avec une vraie bibliothèque PDF (iText, Apache PDFBox, etc.)
     */
    public byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        // Pour l'instant, on génère un HTML simple qui peut être converti en PDF
        // Dans une vraie implémentation, utilisez iText ou Apache PDFBox
        
        String html = generateInvoiceHtml(invoice);
        
        // Convertir HTML en bytes (dans une vraie implémentation, convertir en PDF)
        // Pour l'instant, on retourne le HTML comme placeholder
        return html.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Génère le HTML de la facture
     */
    private String generateInvoiceHtml(Invoice invoice) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; }");
        html.append(".header { border-bottom: 2px solid #333; padding-bottom: 20px; margin-bottom: 30px; }");
        html.append(".invoice-info { float: right; text-align: right; }");
        html.append(".company-info { margin-bottom: 20px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append(".total { font-size: 18px; font-weight: bold; margin-top: 20px; }");
        html.append(".footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; font-size: 12px; color: #666; }");
        html.append("</style>");
        html.append("</head><body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<div class='company-info'>");
        html.append("<h1>Viridial Real Estate</h1>");
        html.append("<p>123 Rue de la Paix<br>75001 Paris, France</p>");
        html.append("</div>");
        html.append("<div class='invoice-info'>");
        html.append("<h2>FACTURE</h2>");
        html.append("<p><strong>N°:</strong> ").append(invoice.getInvoiceNumber()).append("</p>");
        html.append("<p><strong>Date:</strong> ").append(invoice.getCreatedAt().format(DATE_FORMATTER)).append("</p>");
        html.append("<p><strong>Échéance:</strong> ").append(invoice.getDueDate().format(DATE_FORMATTER)).append("</p>");
        html.append("</div>");
        html.append("</div>");
        
        // Client info
        html.append("<div>");
        html.append("<h3>Facturé à:</h3>");
        html.append("<p>Organisation ID: ").append(invoice.getOrganizationId()).append("</p>");
        html.append("</div>");
        
        // Invoice details
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>Description</th>");
        html.append("<th>Période</th>");
        html.append("<th>Montant HT</th>");
        html.append("<th>TVA</th>");
        html.append("<th>Total TTC</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        html.append("<tr>");
        html.append("<td>Abonnement - ").append(invoice.getSubscription().getPlan().getName()).append("</td>");
        html.append("<td>");
        if (invoice.getBillingPeriodStart() != null && invoice.getBillingPeriodEnd() != null) {
            html.append(invoice.getBillingPeriodStart().format(DATE_FORMATTER))
                .append(" - ")
                .append(invoice.getBillingPeriodEnd().format(DATE_FORMATTER));
        } else {
            html.append("N/A");
        }
        html.append("</td>");
        html.append("<td>").append(invoice.getAmount()).append(" ").append(invoice.getCurrency()).append("</td>");
        html.append("<td>").append(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : "0.00").append(" ").append(invoice.getCurrency()).append("</td>");
        html.append("<td><strong>").append(invoice.getTotalAmount()).append(" ").append(invoice.getCurrency()).append("</strong></td>");
        html.append("</tr>");
        html.append("</tbody>");
        html.append("</table>");
        
        // Total
        html.append("<div class='total' style='text-align: right;'>");
        html.append("<p>Total TTC: <strong>").append(invoice.getTotalAmount()).append(" ").append(invoice.getCurrency()).append("</strong></p>");
        html.append("</div>");
        
        // Status
        html.append("<div style='margin-top: 20px;'>");
        html.append("<p><strong>Statut:</strong> ").append(invoice.getStatus()).append("</p>");
        if (invoice.getPaidAt() != null) {
            html.append("<p><strong>Payé le:</strong> ").append(invoice.getPaidAt().format(DATE_FORMATTER)).append("</p>");
        }
        html.append("</div>");
        
        // Footer
        html.append("<div class='footer'>");
        html.append("<p>Merci pour votre confiance!</p>");
        html.append("<p>Pour toute question, contactez-nous à support@viridial.com</p>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
}

