package com.organicmart.service;

import com.organicmart.dto.FarmerProfileRequest;
import com.organicmart.entity.FarmerProfile;
import com.organicmart.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface FarmerService {
    FarmerProfile completeProfile(User user, FarmerProfileRequest request, MultipartFile photo, MultipartFile aadhaar);
    void uploadCertificate(FarmerProfile profile, MultipartFile certificateFile, String certificateNumber, java.time.LocalDate issueDate, java.time.LocalDate expiryDate);
    FarmerProfile getProfile(User user);
}
