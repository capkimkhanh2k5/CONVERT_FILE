package com.convertfile.controller;

import java.io.IOException;
import java.util.Map;

import com.convertfile.model.dao.JobDAO;
import com.convertfile.service.FileService;
import com.convertfile.service.CloudService.CloudUploadService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        System.out.println("--- UploadServlet: doPost started ---");
        
        try {
            Part part = request.getPart("file");
            String taskType = request.getParameter("taskType"); 
            String fileName = FileService.extractFileName(part);
            System.out.println("File Name: " + fileName + ", Task Type: " + taskType);
            
            if (fileName == null || fileName.isEmpty()) {
                System.out.println("Error: No file selected.");
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<h3>❌ Lỗi: Bạn chưa chọn file!</h3>");
                return;
            }

            HttpSession session = request.getSession();
            Object userIdObj = session.getAttribute("userId");
            long userId = 0;
            if (userIdObj != null) {
                userId = (Long) userIdObj;
            }
            System.out.println("User ID: " + userId);
            
            System.out.println("Uploading to Cloudinary...");
            Map<String, Object> uploadResult = CloudUploadService.uploadFile(part, fileName, taskType);
            System.out.println("Cloudinary Upload Result: " + uploadResult);

            String cloudinaryUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            String cloudinaryFileName = (String) uploadResult.get("original_filename");
            
            Object bytesObj = uploadResult.get("bytes");
            long fileSize = (bytesObj != null) ? ((Number) bytesObj).longValue() : part.getSize();
            System.out.println("Cloudinary URL: " + cloudinaryUrl + ", Public ID: " + publicId + ", File Size: " + fileSize);
            
            System.out.println("Saving job to database...");
            boolean success = JobDAO.createNewJob(
                fileName,
                cloudinaryFileName,
                cloudinaryUrl,
                taskType,
                publicId,
                fileSize,
                userId
            );

            if (success) {
                System.out.println("Job saved successfully. Redirecting to home.jsp.");
                response.sendRedirect(request.getContextPath() + "/home.jsp");
            } else {
                System.out.println("Error: Failed to save job to database.");
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<h3>❌ Lỗi: Không thể lưu thông tin file vào database!</h3>");
            }

        } catch (Exception e) {
            System.out.println("--- UploadServlet: EXCEPTION ---");
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h3>❌ Lỗi Server: " + e.getMessage() + "</h3>");
        }
        System.out.println("--- UploadServlet: doPost finished ---");
    }
}