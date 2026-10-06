package com.organicmart.serviceimpl;

import com.organicmart.dto.RegisterRequest;
import com.organicmart.entity.*;
import com.organicmart.repository.*;
import com.organicmart.service.AuthService;
import com.organicmart.service.MailService;
import com.organicmart.util.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpVerificationRepository otpRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    @Value("${app.otp.length}")
    private int otpLength;

    @Value("${app.otp.enabled:true}")
    private boolean otpEnabled;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("An account with this email already exists.");
        }
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new IllegalStateException("An account with this mobile number already exists.");
        }

        String roleName = ("ROLE_FARMER".equals(request.getRole())) ? "ROLE_FARMER" : "ROLE_CUSTOMER";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not configured: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .roles(roles)
                .build();

        user = userRepository.save(user);

        // Create empty cart for customers (and farmers too, harmless)
        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);

        if (otpEnabled) {
            sendOtpFor(user, "REGISTRATION");
        } else {
            // OTP/email not configured yet - auto-verify so the account is usable immediately.
            // Flip app.otp.enabled=true in application.properties once Gmail SMTP works.
            user.setEnabled(true);
            userRepository.save(user);
        }
        return user;
    }

    @Override
    @Transactional
    public void resendOtp(String email, String purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("No account found for this email."));
        sendOtpFor(user, purpose);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otp, String purpose) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("No account found for this email."));

        OtpVerification record = otpRepository
                .findTopByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(user, purpose)
                .orElseThrow(() -> new IllegalStateException("No OTP request found. Please request a new OTP."));

        if (record.isUsed() || record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("OTP has expired. Please request a new one.");
        }

        if (!record.getOtpCode().equals(otp)) {
            return false;
        }

        record.setUsed(true);
        otpRepository.save(record);

        if ("REGISTRATION".equals(purpose)) {
            user.setEnabled(true);
            userRepository.save(user);
        }

        return true;
    }

    @Override
    @Transactional
    public void initiateForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("No account found for this email."));
        sendOtpFor(user, "FORGOT_PASSWORD");
    }

    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        boolean valid = verifyOtp(email, otp, "FORGOT_PASSWORD");
        if (!valid) {
            throw new IllegalStateException("Invalid OTP.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("No account found for this email."));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void sendOtpFor(User user, String purpose) {
        String otp = OtpUtil.generateOtp(otpLength);
        OtpVerification record = OtpVerification.builder()
                .user(user)
                .otpCode(otp)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .build();
        otpRepository.save(record);
        mailService.sendOtpEmail(user.getEmail(), user.getFullName(), otp, purpose);
    }
}
