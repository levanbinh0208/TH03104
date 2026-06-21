package com.example.phone_store.controller;

import jakarta.servlet.http.HttpSession;
import com.example.phone_store.entity.Order;
import com.example.phone_store.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(userId);
        model.addAttribute("orders", orders);

        return "orders";
    }
}