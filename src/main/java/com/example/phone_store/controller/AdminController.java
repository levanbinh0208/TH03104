package com.example.phone_store.controller;

import jakarta.servlet.http.HttpSession;
import com.example.phone_store.entity.Order;
import com.example.phone_store.entity.OrderDetail;
import com.example.phone_store.entity.Product;
import com.example.phone_store.entity.User;
import com.example.phone_store.service.OrderService;
import com.example.phone_store.service.ProductService;
import com.example.phone_store.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private final ProductService productService;
    private final OrderService   orderService;
    private final UserService    userService;

    public AdminController(ProductService productService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.orderService   = orderService;
        this.userService    = userService;
    }

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("roleName");
        return "ADMIN".equalsIgnoreCase(role);
    }

    private String adminFullName(HttpSession session) {
        Object o = session.getAttribute("fullName");
        return o != null ? o.toString() : "";
    }

    @GetMapping({"/admin", "/admin/dashboard"})
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        List<Product> products = productService.findAll();
        List<Order>   orders   = orderService.findAll();
        List<User>    users    = userService.findAll();

        model.addAttribute("products",      products);
        model.addAttribute("orders",        orders);
        model.addAttribute("totalProducts", products == null ? 0 : products.size());
        model.addAttribute("totalOrders",   orders   == null ? 0 : orders.size());
        model.addAttribute("totalUsers",    users    == null ? 0 : users.size());
        model.addAttribute("fullName",      adminFullName(session));
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String listUsers(HttpSession session, Model model, @RequestParam(required = false) String success, @RequestParam(required = false) String error) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("users",    userService.findAll());
        model.addAttribute("fullName", adminFullName(session));
        if (success != null) model.addAttribute("success", success);
        if (error   != null) model.addAttribute("error",   error);
        return "admin/users";
    }

    @PostMapping("/admin/users/role")
    public String updateUserRole(HttpSession session, @RequestParam Integer userId, @RequestParam String roleName, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        boolean ok = userService.updateRole(userId, roleName);
        if (ok) ra.addAttribute("success", "Cập nhật quyền thành công!");
        else    ra.addAttribute("error",   "Cập nhật quyền thất bại.");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(HttpSession session, @PathVariable Integer id, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        Integer myId = (Integer) session.getAttribute("userId");
        if (myId != null && myId.equals(id)) {
            ra.addAttribute("error", "Không thể xóa tài khoản đang đăng nhập!");
            return "redirect:/admin/users";
        }
        boolean ok = userService.deleteById(id);
        if (ok) ra.addAttribute("success", "Đã xóa người dùng.");
        else    ra.addAttribute("error",   "Xóa thất bại.");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/orders")
    public String listOrders(HttpSession session, Model model, @RequestParam(required = false) String success, @RequestParam(required = false) String error) {
        if (!isAdmin(session)) return "redirect:/login";

        List<Order> orders = orderService.findAll();
        model.addAttribute("orders",    orders);
        model.addAttribute("fullName",  adminFullName(session));
        if (success != null) model.addAttribute("success", success);
        if (error   != null) model.addAttribute("error",   error);
        return "admin/orders";
    }

    @GetMapping("/admin/orders/{id}")
    public String orderDetail(HttpSession session, @PathVariable Integer id, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        Order order = orderService.findById(id);
        if (order == null) return "redirect:/admin/orders";

        List<OrderDetail> details = orderService.findDetailsByOrderId(id);
        model.addAttribute("order",    order);
        model.addAttribute("details",  details);
        model.addAttribute("fullName", adminFullName(session));
        return "admin/order-detail";
    }

    @PostMapping("/admin/orders/status")
    public String updateOrderStatus(HttpSession session, @RequestParam Integer orderId, @RequestParam String status, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        boolean ok = orderService.updateStatus(orderId, status);
        if (ok) ra.addAttribute("success", "Cập nhật trạng thái thành công!");
        else    ra.addAttribute("error",   "Cập nhật thất bại.");
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders/delete/{id}")
    public String deleteOrder(HttpSession session, @PathVariable Integer id, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        boolean ok = orderService.deleteById(id);
        if (ok) ra.addAttribute("success", "Đã xóa đơn hàng #" + id);
        else    ra.addAttribute("error",   "Xóa thất bại.");
        return "redirect:/admin/orders";
    }
}