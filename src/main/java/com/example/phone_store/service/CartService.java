package com.example.phone_store.service;

import com.example.phone_store.entity.CartItem;
import com.example.phone_store.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    public List<CartItem> getCart(Integer userId) {
        return cartMapper.getCartByUserId(userId);
    }

    public void addToCart(Integer userId, Integer productId, Integer quantity) {
        Integer existingQty = cartMapper.findQuantity(userId, productId);
        if (existingQty != null) {
            cartMapper.updateQuantity(userId, productId, existingQty + quantity);
        } else {
            cartMapper.insertCartItem(userId, productId, quantity);
        }
    }

    public void updateQuantity(Integer userId, Integer productId, Integer quantity) {
        if (quantity <= 0) {
            cartMapper.deleteCartItem(userId, productId);
        } else {
            cartMapper.updateQuantity(userId, productId, quantity);
        }
    }

    public void removeItem(Integer userId, Integer productId) {
        cartMapper.deleteCartItem(userId, productId);
    }

    public void clearCart(Integer userId) {
        cartMapper.clearCart(userId);
    }

    public Double getTotal(List<CartItem> cart) {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
}