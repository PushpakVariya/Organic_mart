package com.organicmart.controller;

import com.organicmart.repository.BannerRepository;
import com.organicmart.repository.CategoryRepository;
import com.organicmart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final BannerRepository bannerRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("banners", bannerRepository.findByActiveTrueOrderByDisplayOrderAsc());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("products", productService.getAllActive());
        return "home";
    }

    @GetMapping("/products")
    public String allProducts(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("products",
                (keyword != null && !keyword.isBlank()) ? productService.search(keyword) : productService.getAllActive());
        model.addAttribute("keyword", keyword);
        return "products";
    }

    @GetMapping("/category/{id}")
    public String byCategory(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("products", productService.getByCategory(id));
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "product-detail";
    }
}
