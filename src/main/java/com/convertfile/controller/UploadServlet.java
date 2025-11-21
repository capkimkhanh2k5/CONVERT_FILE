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

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Part part = request.getPart("file");
            String taskType = request.getParameter("taskType");
            String fileName = FileService.extractFileName(part);
            System.out.println("File Name: " + fileName + ", Task Type: " + taskType);

            if (fileName == null || fileName.isEmpty()) {
                System.out.println("Error: No file selected.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\": " + "\"error\", \"message\": " + "\"No file selected\"}");
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
            System.out.println(
                    "Cloudinary URL: " + cloudinaryUrl + ", Public ID: " + publicId + ", File Size: " + fileSize);

            System.out.println("Saving job to database...");
            String fileId = JobDAO.createNewJob(
                    fileName,
                    cloudinaryFileName,
                    cloudinaryUrl,
                    taskType,
                    publicId,
                    fileSize,
                    userId);

            if (fileId != null) {
                // Nếu là khách (userId == 0), lưu fileId vào session
                if (userId == 0) {
                    @SuppressWarnings("unchecked")
                    java.util.List<String> guestFileIds = (java.util.List<String>) session
                            .getAttribute("guestFile_ids");
                    if (guestFileIds == null) {
                        guestFileIds = new java.util.ArrayList<>();
                        session.setAttribute("guestFile_ids", guestFileIds);
                    }
                    guestFileIds.add(fileId);
                    System.out.println("Guest file added to session: " + fileId);
                }

                System.out.println("Job saved successfully. File ID: " + fileId);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter()
                        .write("{\"status\": " + "\"success\", \"message\": " + "\"File uploaded successfully\"}");
            } else {
                System.out.println("Error: Failed to save job to database.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter()
                        .write("{\"status\": " + "\"error\", \"message\": " + "\"Failed to save job to database\"}");
            }

        } catch (Exception e) {
            System.out.println("--- UploadServlet: EXCEPTION ---");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Escape quotes in message if needed, but for now simple message
            response.getWriter()
                    .write("{\"status\": " + "\"error\", \"message\": " + "\"Server Error: " + e.getMessage() + "\"}");
        }
        System.out.println("--- UploadServlet: doPost finished ---");
    }
}