package com.convertfile.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
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
                
                // Lấy thông tin người dùng
                String userId = payload.getSubject();
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String givenName = (String) payload.get("given_name");
                String familyName = (String) payload.get("family_name");
                
                // Kiểm tra email đã được xác thực
                if (!emailVerified) {
                    sendErrorResponse(resp, "Email not verified");
                    return;
                }
                
                // TODO: Kiểm tra user đã tồn tại trong database chưa
                // TODO: Nếu chưa có thì tạo user mới
                // TODO: Nếu có rồi thì cập nhật thông tin (nếu cần)
                
                // Tạo session cho user
                HttpSession session = req.getSession(true);
                session.setAttribute("userId", userId);
                session.setAttribute("email", email);
                session.setAttribute("name", name);
                session.setAttribute("pictureUrl", pictureUrl);
                session.setAttribute("givenName", givenName);
                session.setAttribute("familyName", familyName);
                session.setAttribute("loginMethod", "GOOGLE");
                
                // Set session timeout (30 phút)
                session.setMaxInactiveInterval(30 * 60);
                
                // Gửi response thành công
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(String.format(
                    "{\"success\": true, \"message\": \"Login successful\", " +
                    "\"user\": {\"name\": \"%s\", \"email\": \"%s\"}}", 
                    name, email
                ));
                
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
            message
        ));
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, 
            "GET method not supported. Use POST.");
    }
}