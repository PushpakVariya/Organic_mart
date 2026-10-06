package com.organicmart.repository;

import com.organicmart.entity.FarmerBankDetails;
import com.organicmart.entity.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FarmerBankDetailsRepository extends JpaRepository<FarmerBankDetails, Long> {
    Optional<FarmerBankDetails> findByFarmerProfile(FarmerProfile farmerProfile);
}
