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

    public String createResetToken(User user) {
        tokenMapper.deleteByUserId(user.getUserId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenMapper.insert(prt);
        return token;
    }

    public boolean sendResetPasswordEmail(String email, String token) {
        try {
            String resetLink = "http://localhost:8080/reset-password?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🔐 Đặt lại mật khẩu - PhoneStore");

            String htmlContent = "<div style='font-family: Arial; max-width: 500px; margin: auto; "
                    + "padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                    + "<h2 style='color: #007bff;'>📱 PhoneStore</h2>"
                    + "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu.</p>"
                    + "<p>Nhấn vào nút bên dưới (có hiệu lực trong 15 phút):</p>"
                    + "<a href='" + resetLink + "' style='background: #007bff; color: white; "
                    + "padding: 12px 25px; text-decoration: none; border-radius: 5px; "
                    + "display: inline-block;'>Đặt lại mật khẩu</a>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            return true;

        } catch (MessagingException e) {
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
}