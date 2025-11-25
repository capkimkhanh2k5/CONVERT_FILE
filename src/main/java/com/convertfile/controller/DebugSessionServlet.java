package com.convertfile.controller;

import com.convertfile.config.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Debug servlet để kiểm tra:
 * 1. Session có guestFile_ids không
 * 2. Files trong DB
 * 3. Force trigger session destroy
 */
@WebServlet("/debug-session")
public class DebugSessionServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Debug Session</title>");
        out.println("<style>body{font-family:monospace;padding:20px;background:#1e1e1e;color:#d4d4d4;}</style>");
        out.println("</head><body>");
        
        out.println("<h1>🔍 Session Debug Info</h1><hr>");
        
        // 1. Check current session
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            out.println("<h2>⚠️ No active session</h2>");
        } else {
            out.println("<h2>✅ Active Session</h2>");
            out.println("<p><strong>Session ID:</strong> " + session.getId() + "</p>");
            out.println("<p><strong>Created:</strong> " + new java.util.Date(session.getCreationTime()) + "</p>");
            out.println("<p><strong>Last accessed:</strong> " + new java.util.Date(session.getLastAccessedTime()) + "</p>");
            out.println("<p><strong>Max inactive (sec):</strong> " + session.getMaxInactiveInterval() + "</p>");
            
            // Check guestFile_ids
            @SuppressWarnings("unchecked")
            java.util.List<String> guestFileIds = (java.util.List<String>) session.getAttribute("guestFile_ids");
            
            if (guestFileIds == null || guestFileIds.isEmpty()) {
                out.println("<p>⚠️ <strong>No guestFile_ids in session</strong></p>");
            } else {
                out.println("<p>✅ <strong>guestFile_ids found:</strong> " + guestFileIds.size() + " files</p>");
                out.println("<ul>");
                for (String fileId : guestFileIds) {
                    out.println("<li>" + fileId + "</li>");
                }
                out.println("</ul>");
            }
            
            out.println("<hr>");
            out.println("<form method='post'>");
            out.println("<button type='submit' name='action' value='invalidate' style='padding:10px 20px;font-size:16px;cursor:pointer;'>🔥 Force Destroy Session (Test Cleanup)</button>");
            out.println("</form>");
        }
        
        // 2. Check guest files in DB
        out.println("<hr><h2>📊 Database Status</h2>");
        
        try {
            String sql = "SELECT file_id, original_name, public_id, created_at FROM files WHERE user_id = 0 ORDER BY created_at DESC LIMIT 10";
            
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                int count = 0;
                out.println("<table border='1' cellpadding='10' style='border-collapse:collapse;'>");
                out.println("<tr><th>#</th><th>File ID</th><th>Name</th><th>Public ID</th><th>Created</th></tr>");
                
                while (rs.next()) {
                    count++;
                    out.println("<tr>");
                    out.println("<td>" + count + "</td>");
                    out.println("<td>" + rs.getString("file_id") + "</td>");
                    out.println("<td>" + rs.getString("original_name") + "</td>");
                    out.println("<td>" + rs.getString("public_id") + "</td>");
                    out.println("<td>" + rs.getString("created_at") + "</td>");
                    out.println("</tr>");
                }
                
                out.println("</table>");
                
                if (count == 0) {
                    out.println("<p>ℹ️ No guest files in database</p>");
                }
            }
            
        } catch (Exception e) {
            out.println("<p style='color:red;'>❌ DB Error: " + e.getMessage() + "</p>");
        }
        
        out.println("<hr>");
        out.println("<p><a href='/CONVERT_FILE/home'>← Back to Home</a></p>");
        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("invalidate".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Lấy session ID trước khi invalidate
                String sessionId = session.getId();
                
                // Trigger session destroy (SessionCleanupListener sẽ chạy)
                session.invalidate();
                
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                
                out.println("<!DOCTYPE html><html><head><title>Session Destroyed</title>");
                out.println("<style>body{font-family:monospace;padding:20px;background:#1e1e1e;color:#4ec9b0;}</style>");
                out.println("</head><body>");
                out.println("<h1>✅ Session Destroyed!</h1>");
                out.println("<p>Session ID: " + sessionId + "</p>");
                out.println("<p>SessionCleanupListener should have been triggered.</p>");
                out.println("<hr>");
                out.println("<p><a href='/CONVERT_FILE/debug-session'>Check Status Again</a></p>");
                out.println("<p><a href='/CONVERT_FILE/home'>← Back to Home</a></p>");
                out.println("</body></html>");
            }
        }
    }
}
