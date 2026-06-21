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
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam(value = "username", required = false) String username,
                              @RequestParam(value = "password", required = false) String password, HttpSession session, Model model) {
        if ((username == null || username.trim().isEmpty()) && !(password == null || password.trim().isEmpty())) {
            model.addAttribute("error", "Vui lòng nhập tên đăng nhập.");
            return "login";
        }
        if (password == null || password.trim().isEmpty() && !(username == null || username.trim().isEmpty())) {
            model.addAttribute("error", "Vui lòng nhập mật khẩu.");
            return "login";
        }

        User user = userService.login(username, password);

        if (user == null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            return "login";
        }

        session.setAttribute("loggedInUser", user);
        session.setAttribute("userId",   user.getUserId());
        session.setAttribute("fullName", user.getFullName());
        session.setAttribute("roleName", user.getRoleName());

        if ("ADMIN".equals(user.getRoleName())) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập email.");
            return "forgot-password";
        }

        User user = userService.findByEmail(email.trim());
        if (user == null) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "forgot-password";
        }

        String token = userService.createResetToken(user);
        boolean sent = userService.sendResetPasswordEmail(user.getEmail(), token);

        if (!sent) {
            model.addAttribute("error", "Không thể gửi email. Vui lòng thử lại sau!");
            return "forgot-password";
        }

        model.addAttribute("success", "Link đặt lại mật khẩu đã được gửi đến email của bạn!");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam("token") String token, Model model) {
        if (!userService.isValidResetToken(token)) {
            model.addAttribute("error", "Link không hợp lệ hoặc đã hết hạn!");
            return "forgot-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        if (!userService.isValidResetToken(token)) {
            redirectAttributes.addFlashAttribute("error", "Link không hợp lệ hoặc đã hết hạn!");
            return "redirect:/forgot-password";
        }

        if (newPassword == null || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "redirect:/reset-password?token=" + token;
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/reset-password?token=" + token;
        }

        boolean success = userService.resetPasswordByToken(token, newPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            return "redirect:/reset-password?token=" + token;
        }

        redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}