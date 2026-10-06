package com.organicmart.controller;

import com.organicmart.entity.CertificateStatus;
import com.organicmart.security.AppUserPrincipal;
import com.organicmart.service.AdminService;
import com.organicmart.repository.FarmerCertificateRepository;
import com.organicmart.repository.OrderRepository;
import com.organicmart.repository.UserRepository;
import com.organicmart.repository.DeliveryPartnerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final FarmerCertificateRepository certificateRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryPartnerProfileRepository deliveryPartnerProfileRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/certificates")
    public String pendingCertificates(Model model) {
        model.addAttribute("certificates", certificateRepository.findByStatus(CertificateStatus.PENDING));
        return "admin/certificates";
    }

    @PostMapping("/certificates/{id}/review")
    public String reviewCertificate(@AuthenticationPrincipal AppUserPrincipal principal,
                                     @PathVariable Long id, @RequestParam String decision,
                                     @RequestParam(required = false) String rejectionReason) {
        CertificateStatus status = "APPROVE".equalsIgnoreCase(decision) ? CertificateStatus.APPROVED : CertificateStatus.REJECTED;
        adminService.reviewCertificate(id, status, rejectionReason, principal.getUser().getId());
        return "redirect:/admin/certificates";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/delivery-partners")
    public String deliveryPartners(Model model) {
        model.addAttribute("partners", deliveryPartnerProfileRepository.findByApprovalStatus("PENDING"));
        return "admin/delivery-partners";
    }

    @PostMapping("/delivery-partners/{userId}/approve")
    public String approveDeliveryPartner(@PathVariable Long userId, @RequestParam boolean approve) {
        adminService.approveDeliveryPartner(userId, approve);
        return "redirect:/admin/delivery-partners";
    }
}
