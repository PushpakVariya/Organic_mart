package com.organicmart.serviceimpl;

import com.organicmart.dto.FarmerProfileRequest;
import com.organicmart.entity.*;
import com.organicmart.repository.*;
import com.organicmart.service.FarmerService;
import com.organicmart.service.MailService;
import com.organicmart.util.FileStorageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

    private final FarmerProfileRepository farmerProfileRepository;
    private final FarmerBankDetailsRepository bankDetailsRepository;
    private final FarmerCertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;
    private final MailService mailService;

    @Override
    @Transactional
    public FarmerProfile completeProfile(User user, FarmerProfileRequest request, MultipartFile photo, MultipartFile aadhaar) {
        FarmerProfile profile = farmerProfileRepository.findByUser(user)
                .orElse(FarmerProfile.builder().user(user).build());

        profile.setFarmName(request.getFarmName());
        profile.setVillage(request.getVillage());
        profile.setTaluka(request.getTaluka());
        profile.setDistrict(request.getDistrict());
        profile.setState(request.getState());
        profile.setPinCode(request.getPinCode());

        if (aadhaar != null && !aadhaar.isEmpty()) {
            profile.setAadhaarDocumentUrl(fileStorageUtil.store(aadhaar, "aadhaar"));
        }

        profile = farmerProfileRepository.save(profile);

        FarmerBankDetails bank = bankDetailsRepository.findByFarmerProfile(profile)
                .orElse(FarmerBankDetails.builder().farmerProfile(profile).build());
        bank.setAccountHolderName(request.getAccountHolderName());
        bank.setAccountNumber(request.getAccountNumber());
        bank.setIfscCode(request.getIfscCode());
        bank.setBankName(request.getBankName());
        bank.setUpiId(request.getUpiId());
        bankDetailsRepository.save(bank);

        if (photo != null && !photo.isEmpty()) {
            user.setProfilePhotoUrl(fileStorageUtil.store(photo, "profile-photos"));
        }
        user.setProfileCompleted(true);
        userRepository.save(user);

        return profile;
    }

    @Override
    @Transactional
    public void uploadCertificate(FarmerProfile profile, MultipartFile certificateFile, String certificateNumber,
                                   LocalDate issueDate, LocalDate expiryDate) {
        if (certificateFile == null || certificateFile.isEmpty()) {
            throw new IllegalStateException("Please choose a certificate file to upload.");
        }
        String url = fileStorageUtil.store(certificateFile, "certificates");
        FarmerCertificate certificate = FarmerCertificate.builder()
                .farmerProfile(profile)
                .certificateNumber(certificateNumber)
                .certificateFileUrl(url)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .status(CertificateStatus.PENDING)
                .build();
        certificateRepository.save(certificate);
    }

    @Override
    public FarmerProfile getProfile(User user) {
        return farmerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Farmer profile not found. Please complete your profile first."));
    }
}
