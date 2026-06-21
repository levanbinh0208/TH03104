package com.example.phone_store.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.phone_store.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        User user = (User) request.getSession()
                .getAttribute("loggedInUser");

        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRoleName())) {
            response.sendRedirect("/home");
            return false;
        }

        return true;
    }
}