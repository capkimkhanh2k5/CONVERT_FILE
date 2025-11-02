package com.convertfile.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import com.convertfile.dao.UserDAO;
import com.convertfile.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        if (username.isEmpty() || password.isEmpty() || !checkFormatEmail(email)) {
            if(!checkFormatEmail(email)){
                request.setAttribute("errorMessage", "Email format is invalid!");
            } else{
                request.setAttribute("errorMessage", "Please fill in all information!");
            }
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreated_at(LocalDateTime.now());

        userDAO.insertUser(user);

        // Nếu OK
        request.setAttribute("successMessage", "Resigter successfully! Please login!");
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    private boolean checkFormatEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }
}
