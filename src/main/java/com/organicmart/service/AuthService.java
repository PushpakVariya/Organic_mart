package com.organicmart.service;

import com.organicmart.dto.RegisterRequest;
import com.organicmart.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
    void resendOtp(String email, String purpose);
    boolean verifyOtp(String email, String otp, String purpose);
    void initiateForgotPassword(String email);
    void resetPassword(String email, String otp, String newPassword);
}
