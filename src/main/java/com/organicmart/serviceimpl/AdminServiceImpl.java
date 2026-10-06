package com.organicmart.serviceimpl;

import com.organicmart.entity.*;
import com.organicmart.repository.*;
import com.organicmart.service.AdminService;
import com.organicmart.service.MailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final FarmerCertificateRepository certificateRepository;
    private final DeliveryPartnerProfileRepository deliveryPartnerProfileRepository;
    private final MailService mailService;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFarmers", userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_FARMER"))).count());
        stats.put("totalCustomers", userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_CUSTOMER"))).count());
        stats.put("totalProducts", productRepository.count());

        List<Order> allOrders = orderRepository.findAll();
        stats.put("totalOrders", allOrders.size());

        BigDecimal revenue = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", revenue);

        stats.put("pendingCertificates", certificateRepository.findByStatus(CertificateStatus.PENDING).size());
        stats.put("pendingDeliveryPartners", deliveryPartnerProfileRepository.findByApprovalStatus("PENDING").size());

        return stats;
    }

    @Override
    @Transactional
    public void reviewCertificate(Long certificateId, CertificateStatus status, String rejectionReason, Long reviewerId) {
        FarmerCertificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalStateException("Certificate not found."));
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalStateException("Reviewer not found."));

        cert.setStatus(status);
        cert.setReviewedBy(reviewer);
        cert.setReviewedAt(LocalDateTime.now());
        if (status == CertificateStatus.REJECTED) {
            cert.setRejectionReason(rejectionReason);
        }
        certificateRepository.save(cert);

        User farmerUser = cert.getFarmerProfile().getUser();
        mailService.sendCertificateStatusEmail(farmerUser.getEmail(), farmerUser.getFullName(),
                status.name(), rejectionReason);
    }

    @Override
    @Transactional
    public void approveDeliveryPartner(Long userId, boolean approve) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found."));
        DeliveryPartnerProfile profile = deliveryPartnerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Delivery partner profile not found."));
        profile.setApprovalStatus(approve ? "APPROVED" : "REJECTED");
        deliveryPartnerProfileRepository.save(profile);
    }
}
