package com.example.phone_store.controller;

import jakarta.servlet.http.HttpSession;
import com.example.phone_store.entity.Product;
import com.example.phone_store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping({"/", "/home"})
    public String showHome(
            @RequestParam(value = "search", required = false) String search,
            HttpSession session, Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        List<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search.trim());
            model.addAttribute("searchTerm", search.trim());
        } else {
            products = productService.getAllProductsHot();
        }

        model.addAttribute("products", products);
        return "home";
    }
}