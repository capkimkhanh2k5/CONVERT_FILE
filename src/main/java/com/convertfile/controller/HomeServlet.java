package com.convertfile.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Nếu không có session -> Guest
        if (session == null) {
            request.setAttribute("username", "GUEST");
        } else {
            String username = (String) session.getAttribute("username");
            if (username == null) {
                request.setAttribute("username", "GUEST");
            } else {
                request.setAttribute("username", username);
            }
        }

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
