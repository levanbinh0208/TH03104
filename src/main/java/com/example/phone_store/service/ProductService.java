package com.example.phone_store.service;

import com.example.phone_store.entity.Product;
import com.example.phone_store.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }

    public List<Product> getAllProductsHot() {
        return productMapper.getAllProductsHot();
    }

    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }

    public Product getProductById(Integer productId) {
        return productMapper.getProductById(productId);
    }

    public void saveProduct(Product product) {
        if (product.getProductId() == null) {
            productMapper.insertProduct(product);
        } else {
            productMapper.updateProduct(product);
        }
    }

    public Product getById(Integer productId) {
        return productMapper.findById(productId);
    }

    public void deleteProduct(Integer productId) {
        productMapper.deleteProduct(productId);
    }

    public List<Product> getProductsByCategory(Integer categoryId) { return productMapper.getProductsByCategory(categoryId); }


    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<Product> findAll() {
        return productMapper.findAll();
    }
}