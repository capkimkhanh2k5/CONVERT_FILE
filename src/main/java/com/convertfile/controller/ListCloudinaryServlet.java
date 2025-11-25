package com.convertfile.controller;

import com.convertfile.service.CloudService.CloudConnect;
import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * LIST FILES TRÊN CLOUDINARY
 * URL: http://localhost:8080/CONVERT_FILE/list-cloudinary
 */
@WebServlet("/list-cloudinary")
public class ListCloudinaryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Cloudinary Files</title>");
        out.println("<style>");
        out.println("body { font-family: monospace; padding: 20px; background: #1e1e1e; color: #d4d4d4; }");
        out.println(".success { color: #4ec9b0; }");
        out.println(".error { color: #f48771; }");
        out.println(".info { color: #9cdcfe; }");
        out.println(".file-item { margin: 15px 0; padding: 15px; border-left: 3px solid #569cd6; background: #252526; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h1>☁️ Files trên Cloudinary</h1>");
        out.println("<hr>");
        
        try {
            Cloudinary cloudinary = CloudConnect.getInstance();
            
            // List files trong folder convertfile/
            out.println("<h3 class='info'>📁 Folder: convertfile/</h3>");
            
            ApiResponse result = cloudinary.api().resources(ObjectUtils.asMap(
                "type", "upload",
                "prefix", "convertfile/",
                "resource_type", "raw",
                "max_results", 100
            ));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
            
            if (resources == null || resources.isEmpty()) {
                out.println("<p class='success'>✅ Không có files nào trong folder convertfile/</p>");
            } else {
                out.println("<p><strong>Tổng cộng:</strong> " + resources.size() + " files</p>");
                out.println("<hr>");
                
                for (int i = 0; i < resources.size(); i++) {
                    Map<String, Object> resource = resources.get(i);
                    
                    String publicId = (String) resource.get("public_id");
                    String format = (String) resource.get("format");
                    Object bytesObj = resource.get("bytes");
                    long bytes = (bytesObj != null) ? ((Number) bytesObj).longValue() : 0;
                    String createdAt = (String) resource.get("created_at");
                    String secureUrl = (String) resource.get("secure_url");
                    
                    out.println("<div class='file-item'>");
                    out.println("<p><strong>File #" + (i+1) + "</strong></p>");
                    out.println("<p><strong>Public ID:</strong> " + publicId + "</p>");
                    out.println("<p><strong>Format:</strong> " + format + "</p>");
                    out.println("<p><strong>Size:</strong> " + (bytes / 1024) + " KB</p>");
                    out.println("<p><strong>Created:</strong> " + createdAt + "</p>");
                    out.println("<p><strong>URL:</strong> <a href='" + secureUrl + "' target='_blank' style='color: #569cd6;'>" + secureUrl + "</a></p>");
                    
                    // Form để xóa file này
                    out.println("<form method='POST' style='margin-top: 10px;'>");
                    out.println("<input type='hidden' name='public_id' value='" + publicId + "'>");
                    out.println("<button type='submit' style='background: #c5c5c5; color: #1e1e1e; border: none; padding: 8px 16px; cursor: pointer; font-weight: bold;'>🗑️ XÓA FILE NÀY</button>");
                    out.println("</form>");
                    
                    out.println("</div>");
                }
            }
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error: " + e.getMessage() + "</p>");
            out.println("<pre style='color: #f48771;'>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("<hr>");
        out.println("<p><a href='/CONVERT_FILE/home' style='color: #569cd6;'>← Back to Home</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String publicId = request.getParameter("public_id");
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Delete Result</title>");
        out.println("<meta http-equiv='refresh' content='3;url=/CONVERT_FILE/list-cloudinary'>");
        out.println("<style>");
        out.println("body { font-family: monospace; padding: 20px; background: #1e1e1e; color: #d4d4d4; }");
        out.println(".success { color: #4ec9b0; }");
        out.println(".error { color: #f48771; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h1>🗑️ Xóa File</h1>");
        out.println("<hr>");
        
        try {
            Cloudinary cloudinary = CloudConnect.getInstance();
            
            out.println("<p><strong>Public ID:</strong> " + publicId + "</p>");
            out.println("<p>⏳ Đang xóa...</p>");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                "resource_type", "raw",
                "type", "upload",
                "invalidate", true
            ));
            
            String resultStatus = (String) result.get("result");
            
            if ("ok".equals(resultStatus)) {
                out.println("<p class='success'>✅ XÓA THÀNH CÔNG!</p>");
            } else if ("not found".equals(resultStatus)) {
                out.println("<p class='success'>✅ File không tồn tại (có thể đã xóa rồi)</p>");
            } else {
                out.println("<p class='error'>⚠️ Kết quả: " + resultStatus + "</p>");
            }
            
            out.println("<p>🔄 Tự động reload sau 3 giây...</p>");
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error: " + e.getMessage() + "</p>");
            out.println("<pre style='color: #f48771;'>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("<hr>");
        out.println("<p><a href='/CONVERT_FILE/list-cloudinary' style='color: #569cd6;'>← Back to List</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
