package com.convertfile.controller;

import java.io.IOException;

import com.convertfile.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final static long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Please fill in all information!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (userDAO.checkUser(username, password)) {
            request.getSession().setAttribute("username", username);
            response.sendRedirect("upload.jsp");
        } else {
            request.setAttribute("errorMessage", "Username or password is invalid!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
