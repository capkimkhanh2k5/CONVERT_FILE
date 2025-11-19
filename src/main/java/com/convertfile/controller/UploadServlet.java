package com.convertfile.controller;

import java.io.IOException;
import java.util.Map;

import com.convertfile.service.CloudService.CloudUploadService;
import com.convertfile.model.dao.JobDAO;
import com.convertfile.service.FileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 50,
    maxRequestSize = 1024 * 1024 * 60
)
public class UploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Lấy file và tham số
            Part part = request.getPart("file");
            String taskType = request.getParameter("taskType"); 
            String fileName = FileService.extractFileName(part);
            
            // 2. Kiểm tra file
            if (fileName == null || fileName.isEmpty()) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<h3>❌ Lỗi: Bạn chưa chọn file!</h3>");
                return;
            }
            
            // 3. Upload lên Cloudinary
            Map<String, Object> uploadResult = CloudUploadService.uploadFile(part, fileName, taskType);
            
            // 4. Lấy thông tin từ kết quả upload
            String cloudinaryUrl = (String) uploadResult.get("secure_url"); // URL HTTPS
            String publicId = (String) uploadResult.get("public_id");       // ID để xóa sau này
            String cloudinaryFileName = (String) uploadResult.get("original_filename");
            
            // 5. Lưu vào Database
            // Thay vì lưu đường dẫn vật lý, bạn lưu URL và public_id
            JobDAO.createNewJob(
                fileName,           // Tên file gốc
                cloudinaryFileName, // Tên file trên cloud
                cloudinaryUrl,      // URL thay vì đường dẫn vật lý
                taskType,
                publicId           // Lưu thêm publicId để xóa sau này
            );

            // 6. Redirect về trang home
            response.sendRedirect(request.getContextPath() + "/home.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h3>❌ Lỗi Server: " + e.getMessage() + "</h3>");
        }
    }
}