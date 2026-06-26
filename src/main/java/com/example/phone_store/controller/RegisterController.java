package com.example.phone_store.controller;

import jakarta.servlet.http.HttpSession;
import com.example.phone_store.entity.User;
import com.example.phone_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegister(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/home";
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam(value = "fullName", defaultValue = "") String fullName,
            @RequestParam(value = "username", defaultValue = "") String username,
            @RequestParam(value = "email",    defaultValue = "") String email,
            @RequestParam(value = "phone",    defaultValue = "") String phone,
            @RequestParam(value = "address",  defaultValue = "") String address,
            @RequestParam(value = "password", defaultValue = "") String password,
            @RequestParam(value = "confirmPassword", defaultValue = "") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        if (fullName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập họ và tên.");
            return "redirect:/register";
        }

        if (!username.matches("[a-zA-Z0-9_]{4,50}")) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập phải có 4–50 ký tự, chỉ gồm chữ cái, số và dấu gạch dưới (_).");
            return "redirect:/register";
        }

        if (email.trim().isEmpty() || !email.contains("@")) {
            redirectAttributes.addFlashAttribute("error", "Email không hợp lệ.");
            return "redirect:/register";
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            return "redirect:/register";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "redirect:/register";
        }

        if (!phone.trim().isEmpty() && !phone.matches("0[0-9]{8,10}")) {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại không hợp lệ (bắt đầu bằng 0, 9–11 chữ số).");
            return "redirect:/register";
        }

        String checkMsg = userService.checkDuplicate(username.trim(), email.trim());
        if (checkMsg != null) {
            redirectAttributes.addFlashAttribute("error", checkMsg);
            return "redirect:/register";
        }

        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setUserName(username.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPhone(phone.trim().isEmpty() ? null : phone.trim());
        newUser.setAddress(address.trim().isEmpty() ? null : address.trim());
        newUser.setPasswordHash(password);
        newUser.setRoleName("USER");

        boolean ok = userService.register(newUser);
        if (!ok) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại!");
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}