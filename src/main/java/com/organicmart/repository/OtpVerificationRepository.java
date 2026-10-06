package com.organicmart.repository;

import com.organicmart.entity.OtpVerification;
import com.organicmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(User user, String purpose);
}
