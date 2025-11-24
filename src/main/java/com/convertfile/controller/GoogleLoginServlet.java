package com.convertfile.controller;

import com.convertfile.bo.UserBO;
import com.convertfile.model.bean.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;

@WebServlet("/google-login")
public class GoogleLoginServlet extends HttpServlet {
    private static String CLIENT_ID;

    @Override
    public void init() throws ServletException {
        try {
            Properties props = new Properties();
            InputStream input = getServletContext()
                    .getResourceAsStream("/WEB-INF/classes/application.properties");

            if (input == null) {
                throw new ServletException("Cannot find application.properties");
            }

            props.load(input);
            CLIENT_ID = props.getProperty("google.client.id");

            System.out.println("✅ Loaded CLIENT_ID: " +
                    (CLIENT_ID != null ? CLIENT_ID.substring(0, 10) + "..." : "NULL"));

        } catch (Exception e) {
            throw new ServletException("Failed to load properties", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Lấy ID token từ request
            String idTokenString = req.getParameter("credential");

            if (idTokenString == null || idTokenString.isEmpty()) {
                sendErrorResponse(resp, "Missing credential parameter");
                return;
            }

            // Xác thực ID token với Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Lấy thông tin người dùng từ Google
                String googleSubId = payload.getSubject(); // Google's unique ID
                String userEmail = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String userName = (String) payload.get("name");
                String userPicture = (String) payload.get("picture");
                String userGivenName = (String) payload.get("given_name");
                String userFamilyName = (String) payload.get("family_name");

                // Kiểm tra email đã được xác thực
                if (!emailVerified) {
                    sendErrorResponse(resp, "Email not verified");
                    return;
                }

                // Single UserBO instance (Refactored)
                UserBO userBO = new UserBO();
                User user = UserBO.getUserByEmail(userEmail);

                if (user == null) {
                    // Tạo user mới cho lần đăng nhập đầu tiên
                    user = new User();
                    user.setEmail(userEmail);
                    user.setUsername(userEmail);
                    // Security: Set random password instead of empty (Google users don't use
                    // password login)
                    user.setPassword(java.util.UUID.randomUUID().toString());
                    user.setPicture_url(userPicture);
                    user.setCreated_at(LocalDateTime.now());

                    userBO.insertUser(user);

                    // Critical: Re-fetch user to get database-generated user_id
                    user = UserBO.getUserByEmail(userEmail);
                } else {
                    // Cập nhật thông tin người dùng nếu có thay đổi (null-safe)
                    boolean needsUpdate = false;

                    if (userPicture != null && !userPicture.equals(user.getPicture_url())) {
                        user.setPicture_url(userPicture);
                        needsUpdate = true;
                    }

                    if (!userEmail.equals(user.getUsername())) {
                        user.setUsername(userEmail);
                        needsUpdate = true;
                    }

                    if (needsUpdate) {
                        userBO.updateUserInfo(user);
                    }
                }

                // Tạo session cho user
                HttpSession session = req.getSession(true);

                //Use database user_id, not Google's sub ID
                session.setAttribute("userId", user.getId()); // Database primary key
                session.setAttribute("googleSubId", googleSubId); // Google unique ID (optional)
                session.setAttribute("useremail", userEmail);
                session.setAttribute("username", userName);
                session.setAttribute("userpicture", userPicture);
                session.setAttribute("givenName", userGivenName);
                session.setAttribute("familyName", userFamilyName);
                session.setAttribute("loginMethod", "GOOGLE");

                // Set session timeout (30 phút)
                session.setMaxInactiveInterval(30 * 60);

                // CLEANUP: Remove Google's g_state cookie which causes Tomcat warnings
                Cookie killCookie = new Cookie("g_state", "");
                killCookie.setMaxAge(0);
                killCookie.setPath("/");
                resp.addCookie(killCookie);

                // Gửi response thành công
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(String.format(
                        "{\"success\": true, \"message\": \"Login successful\", " +
                                "\"user\": {\"name\": \"%s\", \"email\": \"%s\"}}",
                        userName, userEmail));

            } else {
                sendErrorResponse(resp, "Invalid ID token");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, "Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Gửi response lỗi dưới dạng JSON
     */
    private void sendErrorResponse(HttpServletResponse resp, String message)
            throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write(String.format(
                "{\"success\": false, \"error\": \"%s\"}",
                message));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Properties props = new Properties();
        props.load(getServletContext().getResourceAsStream("/WEB-INF/classes/application.properties"));

        req.setAttribute("googleClientId", props.getProperty("google.client.id"));

        req.getRequestDispatcher("/auth.jsp").forward(req, resp);
    }
}