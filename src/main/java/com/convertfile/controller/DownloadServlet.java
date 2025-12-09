package com.convertfile.controller;

import com.convertfile.model.dao.FileDAO;
import com.convertfile.model.bean.Files;
import com.convertfile.service.CloudService.CloudUploadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy file_id từ tham số URL
        String fileId = request.getParameter("fileId");
        if (fileId == null || fileId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing fileId parameter");
            return;
        }

        try {
            // 2. Lấy file info từ DB
            FileDAO fileDAO = new FileDAO();
            Files file = fileDAO.getFileByID(fileId);

            if (file == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found");
                return;
            }

            // 3. ✅ TẠO PUBLIC URL TỪ PUBLIC_ID
            String publicId = file.getPublic_id();
            String publicUrl = CloudUploadService.generateSignedUrl(publicId);

            System.out.println("📥 Downloading file: " + publicId);

            // 4. DOWNLOAD VÀ STREAM FILE VỀ BROWSER
            try (java.io.InputStream in = new java.net.URI(publicUrl).toURL().openStream();
                    java.io.OutputStream out = response.getOutputStream()) {

                // Set response headers để trigger download
                response.setContentType("application/octet-stream");
                String fileName = file.getSaved_name();
                // ✅ SECURITY: Sanitize filename to prevent header injection
                String safeFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + safeFileName + "\"");

                // Stream file từ Cloudinary về browser
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();

                System.out.println("✅ File downloaded successfully: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Download failed: " + e.getMessage());
        }
    }
}
