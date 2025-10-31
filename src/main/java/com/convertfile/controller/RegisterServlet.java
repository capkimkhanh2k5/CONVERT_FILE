package com.convertfile.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        // TODO: Sau này sẽ lưu vào CSDL (UserDAO)
        // Tạm thời giả lập
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Nếu OK
        request.setAttribute("successMessage", "Đăng ký thành công! Hãy đăng nhập.");
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
