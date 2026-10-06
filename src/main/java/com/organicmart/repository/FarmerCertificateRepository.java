package com.organicmart.repository;

import com.organicmart.entity.CertificateStatus;
import com.organicmart.entity.FarmerCertificate;
import com.organicmart.entity.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FarmerCertificateRepository extends JpaRepository<FarmerCertificate, Long> {
    List<FarmerCertificate> findByStatus(CertificateStatus status);
    Optional<FarmerCertificate> findTopByFarmerProfileOrderByUploadedAtDesc(FarmerProfile farmerProfile);
    List<FarmerCertificate> findByFarmerProfile(FarmerProfile farmerProfile);
}
