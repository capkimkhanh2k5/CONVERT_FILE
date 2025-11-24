package com.convertfile.controller;

import com.convertfile.bo.UserBO;
import com.convertfile.service.EmailSerive.EmailService;
import com.convertfile.service.EmailSerive.OTPService;
import com.convertfile.service.EmailSerive.OTPService.OTPResult;
import com.convertfile.service.EmailSerive.OTPService.OTPType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet to handle Forgot Password flow with OTP verification
 * Refactored to use centralized OTPService
 */
@WebServlet("/forgot-password")
public class ForgotPWServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserBO userBO = new UserBO();

    @Override
    protected void doGet(jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response)
            throws jakarta.servlet.ServletException, java.io.IOException {

        // Clear g_state cookie if present
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("g_state".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        request.getRequestDispatcher("/forgotPW.jsp").forward(request, response);
    }

    @Override
    protected void doPost(jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response)
            throws jakarta.servlet.ServletException, java.io.IOException {

        String action = request.getParameter("action");
        String email = request.getParameter("email");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResponse = mapper.createObjectNode();

        if ("sendCode".equals(action)) {
            // Validate email exists in database
            if (email == null || email.trim().isEmpty()) {
                jsonResponse.put("error", true);
                jsonResponse.put("message", "Email is required");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            email = email.trim();

            if (!userBO.checkEmailExist(email)) {
                jsonResponse.put("error", true);
                jsonResponse.put("message", "Email not found");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Send OTP using OTPService
            HttpSession session = request.getSession();
            OTPResult otpResult = OTPService.sendOTP(session, email, OTPType.FORGOT_PASSWORD);

            if (otpResult.isSuccess()) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", otpResult.getMessage());
            } else {
                jsonResponse.put("error", true);
                jsonResponse.put("message", otpResult.getMessage());
            }

            response.getWriter().write(jsonResponse.toString());

        } else if ("verifyCode".equals(action)) {
            String code = request.getParameter("code");

            if (code == null || code.trim().isEmpty()) {
                jsonResponse.put("error", true);
                jsonResponse.put("message", "Verification code is required");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Verify OTP using OTPService
            HttpSession session = request.getSession();
            OTPResult otpResult = OTPService.verifyOTP(session, code.trim(), OTPType.FORGOT_PASSWORD);

            if (otpResult.isSuccess()) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", otpResult.getMessage());
            } else {
                jsonResponse.put("error", true);
                jsonResponse.put("message", otpResult.getMessage());
            }

            response.getWriter().write(jsonResponse.toString());

        } else if ("resetPassword".equals(action)) {
            HttpSession session = request.getSession();

            // Check if password reset was verified
            Boolean verified = (Boolean) session.getAttribute("password_reset_verified");
            if (verified == null || !verified) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Please verify OTP first");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            String newPassword = request.getParameter("newPassword");

            if (newPassword == null || newPassword.trim().isEmpty()) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "New password is required");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Update password
            boolean checkChangePW = userBO.updatePassword(email, newPassword);

            if (!checkChangePW) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to reset password. Please try again.");
            } else {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Password reset successfully");

                // Clear verification status
                session.removeAttribute("password_reset_verified");

                // Send warning email
                try {
                    EmailService.sendPasswordChangeWarning(email);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Don't fail the response if email fails, just log it
                }
            }

            response.getWriter().write(jsonResponse.toString());
        }
    }
}
