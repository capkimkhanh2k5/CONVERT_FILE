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
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 60)
public class UploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("\n========================================");
        System.out.println("🚀 UploadServlet: doPost started");
        System.out.println("========================================");

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            System.out.println("📦 Getting file part from request...");
            Part part = request.getPart("file");
            
            if (part == null) {
                System.out.println("❌ Part is NULL - No file uploaded!");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\": \"error\", \"message\": \"No file part found\"}");
                return;
            }
            
            System.out.println("✅ Part received: " + part.getName() + ", Size: " + part.getSize() + " bytes");
            
            String taskType = request.getParameter("taskType");
            System.out.println("📋 Task Type: " + taskType);
            
            String fileName = FileService.extractFileName(part);
            System.out.println("📄 Extracted File Name: " + fileName);

            if (fileName == null || fileName.isEmpty()) {
                System.out.println("❌ Error: File name is empty!");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\": \"error\", \"message\": \"No file selected\"}");
                return;
            }

            HttpSession session = request.getSession(false);
            Object userIdObj = (session != null) ? session.getAttribute("userId") : null;
            long userId = 0;
            if (userIdObj != null) {
                userId = (Long) userIdObj;
            }
            System.out.println("👤 User ID from session: " + userId + " (0 = Guest)");

            System.out.println("☁️ Uploading to Cloudinary...");
            Map<String, Object> uploadResult = CloudUploadService.uploadFile(part, fileName, taskType);
            System.out.println("✅ Cloudinary Upload Success!");
            System.out.println("   Upload Result Keys: " + uploadResult.keySet());

            String cloudinaryUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            String cloudinaryFileName = (String) uploadResult.get("original_filename");

            Object bytesObj = uploadResult.get("bytes");
            long fileSize = (bytesObj != null) ? ((Number) bytesObj).longValue() : part.getSize();
            
            System.out.println("📊 Cloudinary Details:");
            System.out.println("   • URL: " + cloudinaryUrl);
            System.out.println("   • Public ID: " + publicId);
            System.out.println("   • Filename: " + cloudinaryFileName);
            System.out.println("   • Size: " + fileSize + " bytes");

            System.out.println("💾 Saving job to database...");
            String fileId = JobDAO.createNewJob(
                    fileName,
                    cloudinaryFileName,
                    cloudinaryUrl,
                    taskType,
                    publicId,
                    fileSize,
                    userId);
            
            System.out.println("📌 Database returned File ID: " + fileId);

            if (fileId != null) {
                // Nếu là khách (userId == 0), lưu fileId vào session
                if (userId == 0) {
                    HttpSession guestSession = (session != null) ? session : request.getSession(true);
                    if (session == null) {
                        System.out.println("⚠️ No session existed, created new session for guest");
                    }
                    
                    @SuppressWarnings("unchecked")
                    java.util.List<String> guestFileIds = (java.util.List<String>) guestSession
                            .getAttribute("guestFile_ids");
                    if (guestFileIds == null) {
                        guestFileIds = new java.util.ArrayList<>();
                        guestSession.setAttribute("guestFile_ids", guestFileIds);
                        System.out.println("🆕 Created new guest file list in session");
                    }
                    guestFileIds.add(fileId);
                    System.out.println("👻 Guest file added to session: " + fileId + " (Total: " + guestFileIds.size() + ")");
                }

                System.out.println("✅ SUCCESS! File ID: " + fileId);
                if (userId == 0) {
                    HttpSession guestSession = (session != null) ? session : request.getSession(false);
                    if (guestSession != null) {
                        System.out.println("🔑 Guest session ID: " + guestSession.getId());
                    }
                }
                System.out.println("========================================\n");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter()
                        .write("{\"status\": \"success\", \"message\": \"File uploaded successfully\", \"fileId\": \"" + fileId + "\"}");
            } else {
                System.out.println("❌ FAILED! Database returned NULL fileId");
                System.out.println("========================================\n");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter()
                        .write("{\"status\": \"error\", \"message\": \"Failed to save job to database\"}");
            }

        } catch (Exception e) {
            System.out.println("💥 ========================================");
            System.out.println("💥 UploadServlet: EXCEPTION CAUGHT!");
            System.out.println("💥 Exception Type: " + e.getClass().getName());
            System.out.println("💥 Exception Message: " + e.getMessage());
            System.out.println("💥 ========================================");
            e.printStackTrace();
            System.out.println("========================================\n");
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Server Error: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}