package com.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.phone_store.entity.Product;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> getAllProducts();

    List<Product> getAllProductsHot();

    List<Product> searchProducts(@Param("keyword") String keyword);

    Product getProductById(@Param("productId") Integer productId);

    void insertProduct(Product product);

    void updateProduct(Product product);

    void deleteProduct(@Param("productId") Integer productId);

    List<Product> getProductsByCategory(@Param("categoryId") Integer categoryId);

    Product findById(@Param("productId") Integer productId);

    List<Product> findAll();
}