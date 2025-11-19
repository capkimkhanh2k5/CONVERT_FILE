package com.convertfile.controller;

import java.io.File;
import java.io.IOException;

// --- Import chuẩn cho Tomcat 10 ---
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 50,      // 50MB
    maxRequestSize = 1024 * 1024 * 60    // 60MB
)
public class UploadServlet extends HttpServlet {
    
    private static final String SAVE_DIR = "C:\\uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Tạo thư mục nếu chưa có
            File fileSaveDir = new File(SAVE_DIR);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdir();
            }

            // 2. Lấy file và tham số
            Part part = request.getPart("file");
            String taskType = request.getParameter("taskType"); 
            String fileName = extractFileName(part);
            
            // 3. Xử lý lưu file
            if (fileName != null && !fileName.isEmpty()) {
                // Đổi tên file để tránh trùng
                String savedName = System.currentTimeMillis() + "_" + fileName;
                String fullPath = SAVE_DIR + File.separator + savedName;
                
                // Lưu vật lý
                part.write(fullPath); 

                // Lưu vào Database
                com.convertfile.model.dao.JobDAO.createNewJob(fileName, savedName, fullPath, taskType);

                // ===> ĐÂY LÀ CHỖ ĐÚNG ĐỂ CHUYỂN TRANG <===
                // Sau khi lưu xong, quay về trang home.jsp
                response.sendRedirect(request.getContextPath() + "/home.jsp");
                
            } else {
                // Nếu không có file thì báo lỗi
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<h3>❌ Lỗi: Bạn chưa chọn file!</h3>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h3>❌ Lỗi Server: " + e.getMessage() + "</h3>");
        }
    }

    // Hàm phụ lấy tên file
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1).replace("\"", "");
            }
        }
        return "";
    }
}