package com.organicmart.controller;

import com.organicmart.dto.FarmerProfileRequest;
import com.organicmart.dto.ProductRequest;
import com.organicmart.entity.FarmerProfile;
import com.organicmart.entity.User;
import com.organicmart.security.AppUserPrincipal;
import com.organicmart.service.FarmerService;
import com.organicmart.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/farmer")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;
    private final ProductService productService;
    private final com.organicmart.repository.CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        User user = principal.getUser();
        try {
            FarmerProfile profile = farmerService.getProfile(user);
            model.addAttribute("profile", profile);
            model.addAttribute("products", productService.getByFarmer(profile));
        } catch (IllegalStateException e) {
            return "redirect:/farmer/profile/complete";
        }
        return "farmer/dashboard";
    }

    @GetMapping("/profile/complete")
    public String completeProfileForm(Model model) {
        model.addAttribute("farmerProfileRequest", new FarmerProfileRequest());
        return "farmer/complete-profile";
    }

    @PostMapping("/profile/complete")
    public String completeProfile(@AuthenticationPrincipal AppUserPrincipal principal,
                                   @Valid @ModelAttribute FarmerProfileRequest farmerProfileRequest,
                                   BindingResult result,
                                   @RequestParam(required = false) MultipartFile photo,
                                   @RequestParam(required = false) MultipartFile aadhaar) {
        if (result.hasErrors()) {
            return "farmer/complete-profile";
        }
        farmerService.completeProfile(principal.getUser(), farmerProfileRequest, photo, aadhaar);
        return "redirect:/farmer/dashboard";
    }

    @GetMapping("/certificate/upload")
    public String uploadCertificateForm() {
        return "farmer/upload-certificate";
    }

    @PostMapping("/certificate/upload")
    public String uploadCertificate(@AuthenticationPrincipal AppUserPrincipal principal,
                                     @RequestParam MultipartFile certificateFile,
                                     @RequestParam(required = false) String certificateNumber,
                                     @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate issueDate,
                                     @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        FarmerProfile profile = farmerService.getProfile(principal.getUser());
        farmerService.uploadCertificate(profile, certificateFile, certificateNumber, issueDate, expiryDate);
        return "redirect:/farmer/dashboard";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        model.addAttribute("categories", categoryRepository.findAll());
        return "farmer/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@AuthenticationPrincipal AppUserPrincipal principal,
                              @Valid @ModelAttribute ProductRequest productRequest,
                              BindingResult result,
                              @RequestParam(required = false) List<MultipartFile> images,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "farmer/add-product";
        }
        FarmerProfile profile = farmerService.getProfile(principal.getUser());
        productService.createProduct(profile, productRequest, images);
        return "redirect:/farmer/dashboard";
    }

    @PostMapping("/products/{id}/deactivate")
    public String deactivateProduct(@AuthenticationPrincipal AppUserPrincipal principal, @PathVariable Long id) {
        FarmerProfile profile = farmerService.getProfile(principal.getUser());
        productService.deactivate(id, profile);
        return "redirect:/farmer/dashboard";
    }

    @PostMapping("/products/{id}/activate")
    public String activateProduct(@AuthenticationPrincipal AppUserPrincipal principal, @PathVariable Long id) {
        FarmerProfile profile = farmerService.getProfile(principal.getUser());
        productService.activate(id, profile);
        return "redirect:/farmer/dashboard";
    }
}
