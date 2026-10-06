package com.organicmart.serviceimpl;

import com.organicmart.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String fullName, String otp, String purpose) {
        String subject = "OrganicMart - Your OTP Code";
        String body = "<div style='font-family:Arial,sans-serif'>"
                + "<h2 style='color:#2e7d32;'>OrganicMart</h2>"
                + "<p>Hi " + escape(fullName) + ",</p>"
                + "<p>Your OTP for <b>" + escape(purpose) + "</b> is:</p>"
                + "<h1 style='letter-spacing:4px;color:#2e7d32;'>" + otp + "</h1>"
                + "<p>This OTP is valid for a limited time. Do not share it with anyone.</p>"
                + "<p>- Team OrganicMart</p></div>";
        send(toEmail, subject, body);
    }

    @Async
    @Override
    public void sendOrderConfirmationEmail(String toEmail, String fullName, String orderNumber, String amount) {
        String subject = "Order Confirmed - " + orderNumber;
        String body = "<div style='font-family:Arial,sans-serif'>"
                + "<h2 style='color:#2e7d32;'>OrganicMart</h2>"
                + "<p>Hi " + escape(fullName) + ",</p>"
                + "<p>Your order <b>" + escape(orderNumber) + "</b> worth ₹" + escape(amount) + " has been placed successfully.</p>"
                + "<p>We'll notify you as it progresses.</p>"
                + "<p>- Team OrganicMart</p></div>";
        send(toEmail, subject, body);
    }

    @Async
    @Override
    public void sendOrderStatusEmail(String toEmail, String fullName, String orderNumber, String status) {
        String subject = "Order Update - " + orderNumber;
        String body = "<div style='font-family:Arial,sans-serif'>"
                + "<h2 style='color:#2e7d32;'>OrganicMart</h2>"
                + "<p>Hi " + escape(fullName) + ",</p>"
                + "<p>Your order <b>" + escape(orderNumber) + "</b> status has been updated to: <b>" + escape(status) + "</b></p>"
                + "<p>- Team OrganicMart</p></div>";
        send(toEmail, subject, body);
    }

    @Async
    @Override
    public void sendCertificateStatusEmail(String toEmail, String fullName, String status, String reason) {
        String subject = "Organic Certificate " + status;
        String body = "<div style='font-family:Arial,sans-serif'>"
                + "<h2 style='color:#2e7d32;'>OrganicMart</h2>"
                + "<p>Hi " + escape(fullName) + ",</p>"
                + "<p>Your organic certificate has been <b>" + escape(status) + "</b>.</p>"
                + (reason != null && !reason.isBlank() ? "<p>Reason: " + escape(reason) + "</p>" : "")
                + "<p>- Team OrganicMart</p></div>";
        send(toEmail, subject, body);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String escape(String input) {
        if (input == null) return "";
        return input.replace("<", "&lt;").replace(">", "&gt;");
    }
}
