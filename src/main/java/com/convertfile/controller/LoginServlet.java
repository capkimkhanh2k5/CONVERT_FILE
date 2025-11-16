package com.convertfile.controller;

import java.io.IOException;
import java.util.UUID;

import com.convertfile.bo.UserBO;

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

        session.removeAttribute("csrfToken");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String remember = request.getParameter("remember");
        if ("on".equals(remember)) {
            // Tạo cookie dài hạn
            Cookie userCookie = new Cookie("rememberedUser", username);
            userCookie.setMaxAge(30 * 24 * 60 * 60);
            userCookie.setHttpOnly(true);
            userCookie.setSecure(true);
            response.addCookie(userCookie);
        }

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Please fill in all information!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (userBO.checkloginUser(username, password)) {
            request.getSession().setAttribute("username", username);

            String userEmail = userBO.getUserEmailByUsername(username);
            request.getSession().setAttribute("useremail", userEmail);

            request.getRequestDispatcher("home.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Username or password is invalid!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String csrfToken = UUID.randomUUID().toString();
        
        HttpSession session = request.getSession();
        session.setAttribute("csrfToken", csrfToken);

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
