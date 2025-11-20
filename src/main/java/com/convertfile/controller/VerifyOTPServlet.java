package com.convertfile.controller;

import com.convertfile.service.OTPService;
import com.convertfile.service.OTPService.OTPResult;
import com.convertfile.service.OTPService.OTPType;
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
 * Servlet to handle OTP verification for email confirmation during registration
 * Refactored to use centralized OTPService
 */
@WebServlet("/verify-otp")
public class VerifyOTPServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        try {
            // Get OTP from request
            String inputOtp = request.getParameter("otp");

            // Validate OTP parameter
            if (inputOtp == null || inputOtp.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "OTP is required");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            inputOtp = inputOtp.trim();

            // Verify OTP using OTPService
            HttpSession session = request.getSession();
            OTPResult otpResult = OTPService.verifyOTP(session, inputOtp, OTPType.REGISTRATION);

            result.put("success", otpResult.isSuccess());
            result.put("message", otpResult.getMessage());

            // Add additional data if available (e.g., remaining attempts)
            if (!otpResult.getData().isEmpty()) {
                result.putAll(otpResult.getData());
            }

        } catch (Exception e) {
            System.err.println("[VerifyOTPServlet] Unexpected error: " + e.getMessage());
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
