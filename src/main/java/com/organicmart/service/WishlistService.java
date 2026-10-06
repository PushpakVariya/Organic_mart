package com.organicmart.service;

import com.organicmart.entity.User;
import java.util.List;
import com.organicmart.entity.Wishlist;

public interface WishlistService {
    List<Wishlist> getWishlist(User user);
    void toggle(User user, Long productId);
}
