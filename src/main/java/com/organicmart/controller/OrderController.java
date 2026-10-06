package com.organicmart.controller;

import com.organicmart.entity.Address;
import com.organicmart.security.AppUserPrincipal;
import com.organicmart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final com.organicmart.repository.AddressRepository addressRepository;

    @PostMapping("/checkout/place-order")
    public String placeOrder(@AuthenticationPrincipal AppUserPrincipal principal,
                              @RequestParam Long addressId,
                              @RequestParam String paymentMethod,
                              Model model) {
        var order = orderService.placeOrder(principal.getUser(), addressId, paymentMethod);
        return "redirect:/orders/" + order.getOrderNumber();
    }

    @PostMapping("/checkout/add-address")
    public String addAddress(@AuthenticationPrincipal AppUserPrincipal principal,
                              @RequestParam String recipientName, @RequestParam String phone,
                              @RequestParam String addressLine1, @RequestParam(required = false) String addressLine2,
                              @RequestParam String city, @RequestParam String state, @RequestParam String pinCode) {
        Address address = Address.builder()
                .user(principal.getUser())
                .recipientName(recipientName)
                .phone(phone)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .state(state)
                .pinCode(pinCode)
                .build();
        addressRepository.save(address);
        return "redirect:/checkout";
    }

    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        model.addAttribute("orders", orderService.getOrdersForCustomer(principal.getUser()));
        return "customer/orders";
    }

    @GetMapping("/orders/{orderNumber}")
    public String orderDetail(@PathVariable String orderNumber, Model model) {
        model.addAttribute("order", orderService.getByOrderNumber(orderNumber));
        return "customer/order-detail";
    }
}
