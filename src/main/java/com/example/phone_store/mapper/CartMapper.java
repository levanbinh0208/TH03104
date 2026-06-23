package com.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.phone_store.entity.CartItem;

import java.util.List;

@Mapper
public interface CartMapper {

    List<CartItem> getCartByUserId(@Param("userId") Long userId);

    Integer findQuantity(@Param("userId") Long userId, @Param("productId") Integer productId);

    void insertCartItem(@Param("userId") Long userId, @Param("productId") Integer productId, @Param("quantity") Integer quantity);

    void updateQuantity(@Param("userId") Long userId, @Param("productId") Integer productId, @Param("quantity") Integer quantity);

    void deleteCartItem(@Param("userId") Long userId, @Param("productId") Integer productId);

    void clearCart(@Param("userId") Long userId);
}