package com.convertfile.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.json.JSONObject;

import com.convertfile.model.FileInfo;
import com.convertfile.service.FileService;

@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 5, // 5 MB
    maxFileSize = 1024 * 1024 * 50,      // 50 MB
    maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class UploadServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    private final FileService fileService = new FileService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        JSONObject jsonResponse = new JSONObject();

        try {
            Part filePart = request.getPart("file");
            if(filePart == null || filePart.getSize() == 0)
                throw new ServletException("No files have been uploaded!");

            String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            fileService.validateFile(originalName, filePart.getSize());

            String fileId = UUID.randomUUID().toString();
            String savedName = fileId + "_" + originalName;

            // Đọc đường dẫn từ web.xml
            String inputDir = getServletContext().getInitParameter("UPLOAD_INPUT_PATH");
            String appPath = getServletContext().getRealPath("");
            String uploadPath = appPath + File.separator + inputDir;

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String savedFilePath = uploadPath + File.separator + savedName;
            filePart.write(savedFilePath);

            // Lưu metadata
            FileInfo info = new FileInfo();
            info.setFileId(fileId);
            info.setOriginalName(originalName);
            info.setSavedName(savedName);
            info.setFileSize(filePart.getSize() / (1024.0 * 1024.0));
            info.setStatus("UPLOADED");
            info.setFilePath(savedFilePath);
            fileService.saveFileMetaData(info);
            
            // Tạo phản hồi JSON
            jsonResponse.put("statusProgress", "SUCCESS");
            jsonResponse.put("fileId", info.getFileId());
            jsonResponse.put("originalName", info.getOriginalName());
            jsonResponse.put("savedName", info.getSavedName());
            jsonResponse.put("fileSizeMB", String.format("%.2f", info.getFileSize()));
            jsonResponse.put("status", info.getStatus());
            jsonResponse.put("filePath", info.getFilePath());
            jsonResponse.put("message", "Upload File Completed!");

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            jsonResponse.put("statusProgress", "FAILED");
            jsonResponse.put("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();

            System.out.println(jsonResponse.toString());
        }
    }
}
