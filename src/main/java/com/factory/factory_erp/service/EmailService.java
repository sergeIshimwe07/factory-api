package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.response.SupplierOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.from:noreply@factoryerp.com}")
    private String fromEmail;
    
    @Value("${app.name:Factory ERP}")
    private String appName;
    
    /**
     * Send supplier order notification email
     */
    public void sendSupplierOrderNotification(SupplierOrderResponse order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(order.getSupplierEmail());
            helper.setSubject("New Purchase Order: " + order.getId() + " - " + appName);
            
            String htmlContent = buildSupplierOrderEmailHtml(order);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Supplier order notification sent to: {}", order.getSupplierEmail());
        } catch (MessagingException e) {
            log.error("Failed to send supplier order notification email", e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
    
    /**
     * Build HTML content for supplier order email
     */
    private String buildSupplierOrderEmailHtml(SupplierOrderResponse order) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n");
        html.append(".container { max-width: 800px; margin: 0 auto; padding: 20px; }\n");
        html.append(".header { background: linear-gradient(#2c3e50, #34495e); color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }\n");
        html.append(".header h1 { margin: 0; font-size: 24px; }\n");
        html.append(".body { background: #ecf0f1; padding: 20px; }\n");
        html.append(".section { background: white; padding: 15px; margin-bottom: 10px; border-left: 4px solid #3498db; }\n");
        html.append(".order-summary { display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; }\n");
        html.append(".summary-item { background: #f8f9fa; padding: 10px; border-radius: 3px; }\n");
        html.append(".summary-item label { font-weight: bold; color: #34495e; }\n");
        html.append(".summary-item value { color: #2c3e50; display: block; margin-top: 5px; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }\n");
        html.append("thead { background: #3498db; color: white; }\n");
        html.append("th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }\n");
        html.append("tbody tr:nth-child(even) { background: #f9f9f9; }\n");
        html.append(".totals { text-align: right; margin-top: 15px; }\n");
        html.append(".totals-row { display: flex; justify-content: flex-end; margin-bottom: 5px; }\n");
        html.append(".totals-label { font-weight: bold; min-width: 100px; }\n");
        html.append(".total-amount { color: #27ae60; font-weight: bold; font-size: 18px; }\n");
        html.append(".footer { background: #2c3e50; color: white; padding: 15px; text-align: center; border-radius: 0 0 5px 5px; font-size: 12px; }\n");
        html.append(".action-buttons { text-align: center; margin-top: 20px; }\n");
        html.append(".btn { display: inline-block; padding: 10px 20px; margin: 0 5px; background: #3498db; color: white; text-decoration: none; border-radius: 3px; }\n");
        html.append(".btn:hover { background: #2980b9; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class='container'>\n");
        
        // Header
        html.append("<div class='header'>\n");
        html.append("<h1>📦 New Purchase Order Received</h1>\n");
        html.append("</div>\n");
        
        // Order Details
        html.append("<div class='body'>\n");
        html.append("<div class='section'>\n");
        html.append("<h2>Order Details</h2>\n");
        html.append("<div class='order-summary'>\n");
        html.append("<div class='summary-item'>\n");
        html.append("<label>Purchase Order #</label>\n");
        html.append("<value>").append(order.getId()).append("</value>\n");
        html.append("</div>\n");
        
        html.append("<div class='summary-item'>\n");
        html.append("<label>Order Date</label>\n");
        html.append("<value>").append(order.getCreatedAt()).append("</value>\n");
        html.append("</div>\n");
        
        html.append("<div class='summary-item'>\n");
        html.append("<label>Delivery Date</label>\n");
        html.append("<value>").append(order.getDeliveryDate()).append("</value>\n");
        html.append("</div>\n");
        
        html.append("<div class='summary-item'>\n");
        html.append("<label>Order Status</label>\n");
        html.append("<value>").append(order.getStatus().toUpperCase()).append("</value>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        
        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            html.append("<p><strong>Special Instructions:</strong><br/>\n");
            html.append(order.getNotes()).append("</p>\n");
        }
        html.append("</div>\n");
        
        // Ordered Items
        html.append("<div class='section'>\n");
        html.append("<h2>Ordered Items</h2>\n");
        html.append("<table>\n");
        html.append("<thead>\n");
        html.append("<tr>\n");
        html.append("<th>Raw Material</th>\n");
        html.append("<th>Quantity</th>\n");
        html.append("<th>Unit</th>\n");
        html.append("<th>Unit Price</th>\n");
        html.append("<th>Total</th>\n");
        html.append("</tr>\n");
        html.append("</thead>\n");
        html.append("<tbody>\n");
        
        order.getItems().forEach(item -> {
            html.append("<tr>\n");
            html.append("<td>").append(item.getRawMaterialName()).append("</td>\n");
            html.append("<td style='text-align: center;'>").append(item.getQuantity()).append("</td>\n");
            html.append("<td>").append(item.getUnit()).append("</td>\n");
            html.append("<td style='text-align: right;'>$").append(String.format("%.2f", item.getUnitPrice())).append("</td>\n");
            html.append("<td style='text-align: right;'><strong>$").append(String.format("%.2f", item.getTotal())).append("</strong></td>\n");
            html.append("</tr>\n");
        });
        
        html.append("</tbody>\n");
        html.append("</table>\n");
        html.append("</div>\n");
        
        // Totals
        html.append("<div class='section'>\n");
        html.append("<div class='totals'>\n");
        html.append("<div class='totals-row'>\n");
        html.append("<span class='totals-label'>Subtotal:</span>\n");
        html.append("<span>$").append(String.format("%.2f", order.getSubtotal())).append("</span>\n");
        html.append("</div>\n");
        html.append("<div class='totals-row'>\n");
        html.append("<span class='totals-label'>Tax (10%):</span>\n");
        html.append("<span>$").append(String.format("%.2f", order.getTax())).append("</span>\n");
        html.append("</div>\n");
        html.append("<div class='totals-row'>\n");
        html.append("<span class='totals-label total-amount'>Total Amount: $").append(String.format("%.2f", order.getTotal())).append("</span>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        
        // Action Buttons
        html.append("<div class='action-buttons'>\n");
        html.append("<a href='#' class='btn'>View Order Details</a>\n");
        html.append("<a href='#' class='btn'>Confirm Order</a>\n");
        html.append("</div>\n");
        
        html.append("</div>\n");
        
        // Footer
        html.append("<div class='footer'>\n");
        html.append("<p>This is an automated message. Please do not reply to this email.</p>\n");
        html.append("<p>&copy; 2026 ").append(appName).append(". All rights reserved.</p>\n");
        html.append("</div>\n");
        
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
}
