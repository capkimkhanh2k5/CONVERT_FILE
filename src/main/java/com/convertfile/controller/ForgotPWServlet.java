package com.convertfile.controller;

import com.convertfile.bo.UserBO;
import com.convertfile.service.EmailSerive;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;


@WebServlet("/forgot-password")
public class ForgotPWServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserBO userBO = new UserBO();

    @Override
    protected void doGet(jakarta.servlet.http.HttpServletRequest request, 
            jakarta.servlet.http.HttpServletResponse response) 
            throws jakarta.servlet.ServletException, java.io.IOException {

            // Clear cookie lỗi nếu có
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

        if ("sendCode".equals(action)) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonResponse = mapper.createObjectNode();

            if (email != null && userBO.checkEmailExist(email)) {
                // Gửi mã xác nhận đến email
                String otpValue = EmailSerive.generateOTP();
                String htmlContent = EmailSerive.getOtpEmail(otpValue);

                try {
                    EmailSerive.sendEmail(email, "CODE FOR RESET PASSWORD", htmlContent);
                    // Lưu OTP vào Session
                    HttpSession session = request.getSession();

                    //Xoá OTP cũ nếu có
                    session.removeAttribute("otp");
                    session.removeAttribute("email");
                    
                    session.setAttribute("otp", otpValue);
                    session.setAttribute("email", email);
                    session.setMaxInactiveInterval(5 * 60); // 5 minutes

                    jsonResponse.put("success", true);

                } catch (Exception e) {
                    e.printStackTrace();
                    jsonResponse.put("error", true);
                    jsonResponse.put("message", "Failed to send code. Please try again.");
                }
            } else {
                // Email không tồn tại, xử lý lỗi
                jsonResponse.put("error", true);
                jsonResponse.put("message", "Email not found");
            }

            response.getWriter().write(jsonResponse.toString());
        } else if ("verifyCode".equals(action)) {
            HttpSession session = request.getSession();

            String otp = (String) session.getAttribute("otp");
            String emailFromSession = (String) session.getAttribute("email");

            String code = request.getParameter("code");

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonResponse = mapper.createObjectNode();

            if (otp != null && email.equals(emailFromSession) && otp.equals(code)) {
                jsonResponse.put("success", true);
                session.removeAttribute("otp"); // OTP is used, remove it
            } else {
                jsonResponse.put("error", false);
                jsonResponse.put("message", "Invalid verification code.");
            }

            response.getWriter().write(jsonResponse.toString());
        } else if ("resetPassword".equals(action)) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonResponse = mapper.createObjectNode();

            String newPassword = request.getParameter("newPassword");

            boolean checkChangePW = userBO.updatePassword(email, newPassword);

            if(!checkChangePW){
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to reset password. Please try again.");

                response.getWriter().write(jsonResponse.toString());
                return;
            }

            jsonResponse.put("success", true);
            response.getWriter().write(jsonResponse.toString());
        }

    }
}
