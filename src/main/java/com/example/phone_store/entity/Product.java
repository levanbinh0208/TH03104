package com.example.phone_store.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Product {
    private Integer productId;
    private String productName;
    private String description;
    private Long price;
    private Integer quantity;
    private String imageUrl;
    private Integer categoryId;
    private LocalDateTime createdAt;
    private Boolean isFeatured;
}