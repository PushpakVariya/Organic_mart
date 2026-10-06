package com.organicmart.controller;

import com.organicmart.security.AppUserPrincipal;
import com.organicmart.service.CartService;
import com.organicmart.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final WishlistService wishlistService;
    private final com.organicmart.repository.AddressRepository addressRepository;

    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        model.addAttribute("cart", cartService.getCart(principal.getUser()));
        return "customer/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@AuthenticationPrincipal AppUserPrincipal principal,
                             @RequestParam Long productId,
                             @RequestParam(defaultValue = "1") int quantity) {
        cartService.addItem(principal.getUser(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@AuthenticationPrincipal AppUserPrincipal principal,
                              @RequestParam Long productId, @RequestParam int quantity) {
        cartService.updateQuantity(principal.getUser(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@AuthenticationPrincipal AppUserPrincipal principal, @RequestParam Long productId) {
        cartService.removeItem(principal.getUser(), productId);
        return "redirect:/cart";
    }

    @GetMapping("/wishlist")
    public String viewWishlist(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        model.addAttribute("wishlist", wishlistService.getWishlist(principal.getUser()));
        return "customer/wishlist";
    }

    @PostMapping("/wishlist/toggle")
    public String toggleWishlist(@AuthenticationPrincipal AppUserPrincipal principal, @RequestParam Long productId) {
        wishlistService.toggle(principal.getUser(), productId);
        return "redirect:/wishlist";
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        model.addAttribute("cart", cartService.getCart(principal.getUser()));
        model.addAttribute("addresses", addressRepository.findByUser(principal.getUser()));
        return "customer/checkout";
    }
}
