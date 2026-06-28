package com.example.phone_store.controller;

import jakarta.servlet.http.HttpSession;
import com.example.phone_store.entity.CartItem;
import com.example.phone_store.entity.Order;
import com.example.phone_store.entity.OrderDetail;
import com.example.phone_store.entity.User;
import com.example.phone_store.mapper.OrderDetailMapper;
import com.example.phone_store.mapper.OrderMapper;
import com.example.phone_store.service.CartService;
import com.example.phone_store.service.OrderEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderEmailService orderEmailService;

    private Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        List<CartItem> cartItems = cartService.getCart(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.getTotal(cartItems));
        return "cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addToCart(
            @RequestParam("productId") Integer productId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        Long userId = getUserId(session);

        if (userId == null) {
            result.put("success", false);
            result.put("message", "Vui lòng đăng nhập để thêm vào giỏ hàng");
            return result;
        }

        cartService.addToCart(userId, productId, quantity);
        result.put("success", true);
        return result;
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity,
                                 HttpSession session,RedirectAttributes redirectAttributes) {

        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        cartService.updateQuantity(userId, productId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công");
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeItem(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        cartService.removeItem(userId, id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";
        cartService.clearCart(userId);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        List<CartItem> cartItems = cartService.getCart(userId);
        if (cartItems.isEmpty()) return "redirect:/cart";
        User user = (User) session.getAttribute("loggedInUser");


        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.getTotal(cartItems));
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String checkout(
            HttpSession session,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("phone") String phone,
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("paymentMethod") String paymentMethod,
            RedirectAttributes ra) {

        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        List<CartItem> cartItems = cartService.getCart(userId);
        if (cartItems.isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống");
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setTotalAmount(cartService.getTotal(cartItems));
        order.setOrderDate(new Date());
        order.setReceiverName(receiverName);
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);

        orderMapper.save(order);

        List<OrderDetail> savedDetails = new ArrayList<>();
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailMapper.save(detail);
            savedDetails.add(detail);
        }

        cartService.clearCart(userId);

        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && user.getEmail() != null && !user.getEmail().isBlank()) {
            orderEmailService.sendOrderConfirmation(user.getEmail(), order, savedDetails);
        }

        ra.addFlashAttribute("successMessage",
                "Đặt hàng thành công! Email xác nhận đã được gửi tới " + (user != null ? user.getEmail() : "bạn") + ".");
        return "redirect:/orders";
    }
}