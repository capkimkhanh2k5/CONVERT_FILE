package com.convertfile.controller;

import com.convertfile.bo.UserBO;
import com.convertfile.service.EmailSerive.OTPService;
import com.convertfile.service.EmailSerive.OTPService.OTPResult;
import com.convertfile.service.EmailSerive.OTPService.OTPType;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet to handle OTP sending for email verification during registration
 * Refactored to use centralized OTPService
 */
@WebServlet("/send-otp")
public class SendOTPServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        try {
            // Get email from request
            String email = request.getParameter("email");

            // Validate email parameter
            if (email == null || email.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Email is required");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            email = email.trim();

            // Validate email format
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                result.put("success", false);
                result.put("message", "Invalid email format");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Check if email is already registered
            UserBO userBO = new UserBO();
            if (userBO.checkEmailExist(email)) {
                result.put("success", false);
                result.put("message", "This email is already registered");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Send OTP using OTPService
            HttpSession session = request.getSession();
            OTPResult otpResult = OTPService.sendOTP(session, email, OTPType.REGISTRATION);

            result.put("success", otpResult.isSuccess());
            result.put("message", otpResult.getMessage());

            // Add additional data if available
            if (!otpResult.getData().isEmpty()) {
                result.putAll(otpResult.getData());
            }

        } catch (Exception e) {
            System.err.println("[SendOTPServlet] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An unexpected error occurred. Please try again.");
        }

        response.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().write("{\"success\": false, \"message\": \"GET method not allowed\"}");
    }
}
