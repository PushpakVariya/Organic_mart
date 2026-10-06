package com.organicmart.service;

public interface MailService {
    void sendOtpEmail(String toEmail, String fullName, String otp, String purpose);
    void sendOrderConfirmationEmail(String toEmail, String fullName, String orderNumber, String amount);
    void sendOrderStatusEmail(String toEmail, String fullName, String orderNumber, String status);
    void sendCertificateStatusEmail(String toEmail, String fullName, String status, String reason);
}
