package com.convertfile.controller;

import com.convertfile.config.DBConnect;
import com.convertfile.model.dao.FileDAO;
import com.convertfile.service.CloudService.CloudDeleteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * TEST SERVLET: Xóa tất cả guest files và test Cloudinary deletion
 * URL: http://localhost:8080/CONVERT_FILE/test-cleanup
 */
@WebServlet("/test-cleanup")
public class TestCleanupServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Test Cleanup</title>");
        out.println("<style>");
        out.println("body { font-family: monospace; padding: 20px; background: #1e1e1e; color: #d4d4d4; }");
        out.println(".success { color: #4ec9b0; }");
        out.println(".error { color: #f48771; }");
        out.println(".info { color: #9cdcfe; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h1>🧹 Test Cleanup - Guest Files</h1>");
        out.println("<hr>");
        
        try {
            // Lấy tất cả guest files
            String sql = "SELECT file_id, public_id, original_name, created_at FROM files WHERE user_id = 0 ORDER BY created_at DESC";
            
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                int count = 0;
                int successCount = 0;
                int failCount = 0;
                
                out.println("<h3 class='info'>📋 Found Guest Files:</h3>");
                
                while (rs.next()) {
                    count++;
                    String fileId = rs.getString("file_id");
                    String publicId = rs.getString("public_id");
                    String originalName = rs.getString("original_name");
                    String createdAt = rs.getString("created_at");
                    
                    out.println("<div style='margin: 20px 0; padding: 10px; border-left: 3px solid #569cd6;'>");
                    out.println("<p><strong>File #" + count + ":</strong> " + originalName + "</p>");
                    out.println("<p><strong>ID:</strong> " + fileId + "</p>");
                    out.println("<p><strong>Public ID:</strong> " + publicId + "</p>");
                    out.println("<p><strong>Created:</strong> " + createdAt + "</p>");
                    
                    // Xóa trên Cloudinary
                    out.println("<p>🗑️ Deleting from Cloudinary... ");
                    out.flush();
                    
                    boolean cloudDeleted = CloudDeleteService.deleteFile(publicId);
                    
                    if (cloudDeleted) {
                        out.println("<span class='success'>✅ SUCCESS</span></p>");
                        
                        // Xóa trong DB
                        out.println("<p>🗑️ Deleting from database... ");
                        out.flush();
                        
                        boolean dbDeleted = FileDAO.deleteFileById(fileId);
                        
                        if (dbDeleted) {
                            out.println("<span class='success'>✅ SUCCESS</span></p>");
                            successCount++;
                        } else {
                            out.println("<span class='error'>❌ FAILED</span></p>");
                            failCount++;
                        }
                    } else {
                        out.println("<span class='error'>❌ FAILED</span></p>");
                        failCount++;
                    }
                    
                    out.println("</div>");
                }
                
                if (count == 0) {
                    out.println("<p class='info'>ℹ️ No guest files found</p>");
                } else {
                    out.println("<hr>");
                    out.println("<h3>📊 Summary:</h3>");
                    out.println("<p><strong>Total files:</strong> " + count + "</p>");
                    out.println("<p class='success'><strong>✅ Deleted:</strong> " + successCount + "</p>");
                    out.println("<p class='error'><strong>❌ Failed:</strong> " + failCount + "</p>");
                }
            }
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("<hr>");
        out.println("<p><a href='/CONVERT_FILE/home'>← Back to Home</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
