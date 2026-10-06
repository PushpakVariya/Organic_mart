package com.organicmart.service;

import com.organicmart.entity.Cart;
import com.organicmart.entity.User;

public interface CartService {
    Cart getCart(User user);
    void addItem(User user, Long productId, int quantity);
    void updateQuantity(User user, Long productId, int quantity);
    void removeItem(User user, Long productId);
    void clearCart(User user);
}
