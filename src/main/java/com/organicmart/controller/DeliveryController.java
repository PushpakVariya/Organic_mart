package com.organicmart.controller;

import com.organicmart.entity.OrderStatus;
import com.organicmart.security.AppUserPrincipal;
import com.organicmart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final OrderService orderService;
    private final com.organicmart.repository.OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        model.addAttribute("orders", orderRepository.findByDeliveryPartnerOrderByPlacedAtDesc(principal.getUser()));
        return "delivery/dashboard";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status,
                                @RequestParam(required = false) String remarks) {
        orderService.updateStatus(id, status, remarks);
        return "redirect:/delivery/dashboard";
    }
}
