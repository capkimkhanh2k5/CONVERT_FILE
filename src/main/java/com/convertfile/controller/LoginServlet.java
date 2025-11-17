package com.convertfile.controller;

import java.io.IOException;
import java.util.UUID;

import com.convertfile.bo.UserBO;
import com.convertfile.service.PropertiesService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final static long serialVersionUID = 1L;
    private final UserBO userBO = new UserBO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        
        // Lấy token từ form 
        String submittedToken = request.getParameter("csrfToken");
        
        // Lấy token từ session
        String sessionToken = (String) session.getAttribute("csrfToken");
        
        if (submittedToken == null || !submittedToken.equals(sessionToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        // Xóa token 
        session.removeAttribute("csrfToken");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            session.setAttribute("errorMessage", "Please fill in all information!");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (userBO.checkloginUser(username, password)) {
            if ("on".equals(remember)) {
                Cookie userCookie = new Cookie("rememberedUser", username);
                userCookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngày
                userCookie.setHttpOnly(true);
                userCookie.setSecure(request.isSecure());
                response.addCookie(userCookie);
            }

            session.setAttribute("username", username);
            String userEmail = userBO.getUserEmailByUsername(username);
            session.setAttribute("useremail", userEmail);
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            session.removeAttribute("errorMessage");

            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            session.setAttribute("errorMessage", "Username or password is invalid!");
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        //Lấy lỗi từ session và đặt vào request ---
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        // Tạo và đặt CSRF token mới cho mỗi lần tải trang login
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);

        String googleClientId = PropertiesService.getGoogleClientId();
        request.setAttribute("googleClientId", googleClientId);

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
