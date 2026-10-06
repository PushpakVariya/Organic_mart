package com.organicmart.serviceimpl;

import com.organicmart.entity.*;
import com.organicmart.repository.*;
import com.organicmart.service.WishlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Wishlist> getWishlist(User user) {
        return wishlistRepository.findByUser(user);
    }

    @Override
    @Transactional
    public void toggle(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found."));
        wishlistRepository.findByUserAndProduct(user, product).ifPresentOrElse(
                wishlistRepository::delete,
                () -> wishlistRepository.save(Wishlist.builder().user(user).product(product).build())
        );
    }
}
