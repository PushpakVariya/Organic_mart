package com.organicmart.service;

import com.organicmart.dto.ProductRequest;
import com.organicmart.entity.FarmerProfile;
import com.organicmart.entity.Product;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
    Product createProduct(FarmerProfile farmerProfile, ProductRequest request, List<MultipartFile> images);
    List<Product> getAllActive();
    List<Product> getByCategory(Long categoryId);
    List<Product> getByFarmer(FarmerProfile farmerProfile);
    List<Product> search(String keyword);
    Product getById(Long id);
    void deactivate(Long productId, FarmerProfile requester);
    void activate(Long productId, FarmerProfile requester);
}
