package com.example.phone_store.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import com.example.phone_store.entity.PasswordResetToken;
import com.example.phone_store.entity.User;
import com.example.phone_store.mapper.PasswordResetTokenMapper;
import com.example.phone_store.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordResetTokenMapper tokenMapper;

    @Autowired
    private JavaMailSender mailSender;


    public List<User> findAll() {
        return userMapper.findAll();
    }

    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    public boolean updateRole(Integer userId, String roleName) {
        return userMapper.updateRole(userId, roleName) > 0;
    }

    public boolean deleteById(Integer userId) {
        return userMapper.deleteById(userId) > 0;
    }

    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    public boolean resetPasswordAndSendEmail(String email) {

        User user = userMapper.findByEmail(email);

        if (user == null) {
            return false;
        }

        String newPassword = generateRandomPassword(10);

        userMapper.updatePassword(user.getUserId(), newPassword);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Mật khẩu mới - PhoneStore");

            String html =
                    "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;"
                            + "padding:20px;border:1px solid #e5e7eb;border-radius:10px;'>"

                            + "<div style='text-align:center;'>"
                            + "<h2 style='color:#2563eb;'>🔐 Khôi phục mật khẩu thành công</h2>"
                            + "</div>"

                            + "<p>Xin chào <b>" + user.getFullName() + "</b>,</p>"

                            + "<p>Hệ thống đã tạo một mật khẩu mới cho tài khoản của bạn.</p>"

                            + "<div style='background:#f3f4f6;padding:15px;"
                            + "border-radius:8px;text-align:center;margin:20px 0;'>"
                            + "<p style='margin:0;color:#6b7280;'>Mật khẩu mới của bạn</p>"
                            + "<h1 style='color:#dc2626;letter-spacing:3px;'>"
                            + newPassword
                            + "</h1>"
                            + "</div>"

                            + "<p>⚠️ Vì lý do bảo mật, vui lòng đăng nhập và thay đổi mật khẩu ngay sau khi đăng nhập.</p>"

                            + "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng liên hệ với bộ phận hỗ trợ.</p>"

                            + "<hr style='border:none;border-top:1px solid #e5e7eb;'>"

                            + "<p style='color:#6b7280;font-size:13px;'>"
                            + "Trân trọng,<br>"
                            + "<b>Phone Store Team 📱</b>"
                            + "</p>"

                            + "</div>";

            helper.setText(html, true);

            mailSender.send(message);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidResetToken(String token) {
        PasswordResetToken prt = tokenMapper.findByToken(token);
        return prt != null && !prt.isExpired();
    }

    public boolean resetPasswordByToken(String token, String newPassword) {
        PasswordResetToken prt = tokenMapper.findByToken(token);

        if (prt == null || prt.isExpired()) {
            return false;
        }
        userMapper.updatePassword(prt.getUser().getUserId(), newPassword);

        tokenMapper.deleteByToken(token);

        return true;
    }

    public String checkDuplicate(String username, String email) {
        if (userMapper.findByUsername(username) != null) {
            return "Tên đăng nhập \"" + username + "\" đã được sử dụng. Vui lòng chọn tên khác.";
        }
        if (userMapper.findByEmail(email) != null) {
            return "Email \"" + email + "\" đã được đăng ký. Bạn có thể đăng nhập hoặc lấy lại mật khẩu.";
        }
        return null;
    }

    public boolean register(User user) {
        try {
            return userMapper.insert(user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void changePassword(Long userId, String newPassword) {
        userMapper.updatePassword(userId, newPassword);
    }
}