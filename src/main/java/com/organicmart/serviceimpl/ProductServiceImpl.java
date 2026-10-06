package com.organicmart.serviceimpl;

import com.organicmart.dto.ProductRequest;
import com.organicmart.entity.*;
import com.organicmart.repository.*;
import com.organicmart.service.ProductService;
import com.organicmart.util.FileStorageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final FileStorageUtil fileStorageUtil;

    @Override
    @Transactional
    public Product createProduct(FarmerProfile farmerProfile, ProductRequest request, List<MultipartFile> images) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalStateException("Invalid category selected."));

        Product product = Product.builder()
                .farmerProfile(farmerProfile)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .weight(request.getWeight())
                .harvestDate(request.getHarvestDate())
                .expiryDate(request.getExpiryDate())
                .active(true)
                .build();

        product = productRepository.save(product);

        if (images != null) {
            boolean first = true;
            for (MultipartFile image : images) {
                if (image == null || image.isEmpty()) continue;
                String url = fileStorageUtil.store(image, "products");
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(url)
                        .isPrimary(first)
                        .build();
                product.getImages().add(productImage);
                first = false;
            }
        }
        productRepository.save(product);

        Inventory inventory = Inventory.builder()
                .product(product)
                .quantityAvailable(request.getStock())
                .build();
        inventoryRepository.save(inventory);

        return product;
    }

    @Override
    public List<Product> getAllActive() {
        return productRepository.findByActiveTrue();
    }

    @Override
    public List<Product> getByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found."));
        return productRepository.findByCategoryAndActiveTrue(category);
    }

    @Override
    public List<Product> getByFarmer(FarmerProfile farmerProfile) {
        return productRepository.findByFarmerProfile(farmerProfile);
    }

    @Override
    public List<Product> search(String keyword) {
        return productRepository.searchByName(keyword);
    }

    @Override
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found."));
    }

    @Override
    @Transactional
    public void deactivate(Long productId, FarmerProfile requester) {
        Product product = getById(productId);
        if (!product.getFarmerProfile().getId().equals(requester.getId())) {
            throw new IllegalStateException("You are not authorized to modify this product.");
        }
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void activate(Long productId, FarmerProfile requester) {
        Product product = getById(productId);
        if (!product.getFarmerProfile().getId().equals(requester.getId())) {
            throw new IllegalStateException("You are not authorized to modify this product.");
        }
        product.setActive(true);
        productRepository.save(product);
    }
}
