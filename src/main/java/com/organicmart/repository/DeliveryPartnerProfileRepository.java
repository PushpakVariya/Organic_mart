package com.organicmart.repository;
import com.organicmart.entity.DeliveryPartnerProfile;
import com.organicmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryPartnerProfileRepository extends JpaRepository<DeliveryPartnerProfile, Long> {
    Optional<DeliveryPartnerProfile> findByUser(User user);
    List<DeliveryPartnerProfile> findByApprovalStatus(String status);
}
