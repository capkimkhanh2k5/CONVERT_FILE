package com.convertfile.controller;

import com.convertfile.model.dao.ConnectDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    // Sử dụng cùng thư mục với FileWorker
    private static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "uploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy tên file gốc từ tham số URL (VD: BaoCao.pdf)
        String originalFileName = request.getParameter("file");
        if (originalFileName == null || originalFileName.isEmpty()) {
            response.getWriter().write("Missing file parameter");
            return;
        }

        // 2. Tìm tên file vật lý (saved_name) trong Database
        String savedName = getSavedNameFromDB(originalFileName);
        if (savedName == null) {
            response.getWriter().write("File not found in Database");
            return;
        }

        // 3. Xác định file cần tải về
        // Mặc định tìm file kết quả (.docx) trước
        String docxName = savedName;
        if (savedName.toLowerCase().endsWith(".pdf")) {
            docxName = savedName.substring(0, savedName.length() - 4) + ".docx";
        } else {
            docxName = savedName + ".docx";
        }

        File fileToDownload = new File(UPLOAD_DIR + File.separator + docxName);

        // Nếu file .docx chưa có (do chưa convert xong hoặc lỗi), thì tải file gốc về
        if (!fileToDownload.exists()) {
            fileToDownload = new File(UPLOAD_DIR + File.separator + savedName);
            if (!fileToDownload.exists()) {
                response.getWriter().write("File not found on Server disk");
                return;
            }
        }

        // 4. Cấu hình Header để trình duyệt hiểu đây là file cần tải về
        // Thiết lập MIME type cho Word
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        // Tạo tên file hiển thị khi tải về (Lấy tên gốc thay đuôi .pdf thành .docx)
        String downloadNameDisplay = originalFileName;
        if (originalFileName.toLowerCase().endsWith(".pdf")) {
            downloadNameDisplay = originalFileName.substring(0, originalFileName.length() - 4) + ".docx";
        } else {
            downloadNameDisplay = originalFileName + ".docx";
        }

        // Xử lý tên file có tiếng Việt hoặc dấu cách
        String encodedFileName = URLEncoder.encode(downloadNameDisplay, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        // Thiết lập độ dài file
        response.setContentLength((int) fileToDownload.length());

        // 5. Ghi dữ liệu file ra luồng phản hồi (Response)
        try (FileInputStream in = new FileInputStream(fileToDownload);
                OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096]; // Bộ đệm 4KB
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    // Hàm phụ: Lấy saved_name từ original_name
    private String getSavedNameFromDB(String originalName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectDB.getConnection();
            // Lấy file mới nhất trùng tên
            String sql = "SELECT saved_name FROM files WHERE original_name = ? ORDER BY created_at DESC LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setString(1, originalName);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("saved_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
}