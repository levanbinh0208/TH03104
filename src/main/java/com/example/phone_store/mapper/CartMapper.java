package com.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.phone_store.entity.CartItem;

import java.util.List;

@Mapper
public interface CartMapper {

    List<CartItem> getCartByUserId(@Param("userId") Integer userId);

    Integer findQuantity(@Param("userId") Integer userId, @Param("productId") Integer productId);

    void insertCartItem(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("quantity") Integer quantity);

    void updateQuantity(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("quantity") Integer quantity);

    void deleteCartItem(@Param("userId") Integer userId, @Param("productId") Integer productId);

    void clearCart(@Param("userId") Integer userId);
}