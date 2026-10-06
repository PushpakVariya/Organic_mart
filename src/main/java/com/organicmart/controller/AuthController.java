package com.organicmart.controller;

import com.organicmart.dto.RegisterRequest;
import com.organicmart.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            var user = authService.register(request);
            if (user.isEnabled()) {
                // OTP is currently disabled (app.otp.enabled=false) - account is auto-verified.
                model.addAttribute("successMessage", "Account created! You can log in now.");
                return "auth/login";
            }
            model.addAttribute("email", request.getEmail());
            return "auth/verify-otp";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        try {
            boolean valid = authService.verifyOtp(email, otp, "REGISTRATION");
            if (!valid) {
                model.addAttribute("errorMessage", "Invalid OTP. Please try again.");
                model.addAttribute("email", email);
                return "auth/verify-otp";
            }
            model.addAttribute("successMessage", "Account verified! You can now log in.");
            return "auth/login";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, @RequestParam(defaultValue = "REGISTRATION") String purpose, Model model) {
        authService.resendOtp(email, purpose);
        model.addAttribute("email", email);
        model.addAttribute("infoMessage", "A new OTP has been sent to your email.");
        return "REGISTRATION".equals(purpose) ? "auth/verify-otp" : "auth/reset-password";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        try {
            authService.initiateForgotPassword(email);
            model.addAttribute("email", email);
            return "auth/reset-password";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String otp,
                                 @RequestParam String newPassword, Model model) {
        try {
            authService.resetPassword(email, otp, newPassword);
            model.addAttribute("successMessage", "Password reset successful. Please log in.");
            return "auth/login";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("email", email);
            return "auth/reset-password";
        }
    }
}
