package com.convertfile.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobDAO {

    // 1. Hàm tạo Job mới
    public static boolean createNewJob(String originalName, String savedName, String cloudinaryUrl, 
                                    String taskType, String publicId, long fileSize, long userId) {
    Connection conn = null;
    // Tạo UUID ngẫu nhiên cho file
    String fileId = UUID.randomUUID().toString(); 

    String sqlFile = "INSERT INTO files (file_id, user_id, original_name, saved_name, file_size, " +
                    "file_path, public_id, input_format, current_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'UPLOADED')";
    
    String sqlTask = "INSERT INTO tasks (file_id, task_type, status, progress_percent) " +
                    "VALUES (?, ?, 'WAITING', 0)";

    try {
        conn = ConnectDB.getConnection();
        if (conn == null) return false;

        conn.setAutoCommit(false);

        // Thêm vào bảng FILES
        PreparedStatement psFile = conn.prepareStatement(sqlFile);
        psFile.setString(1, fileId);
        
        // Xử lý User ID
        if (userId > 0) {
            psFile.setLong(2, userId);
        } else {
            psFile.setNull(2, java.sql.Types.BIGINT);
        }
        
        psFile.setString(3, originalName);
        psFile.setString(4, savedName);
        psFile.setLong(5, fileSize);
        psFile.setString(6, cloudinaryUrl);    // URL của Cloudinary
        psFile.setString(7, publicId);         // Public ID từ Cloudinary
        
        // Lấy định dạng file từ tên file (ví dụ: file.docx -> docx)
        String inputFormat = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        psFile.setString(8, inputFormat);
        
        psFile.executeUpdate();

        // Thêm vào bảng TASKS
        PreparedStatement psTask = conn.prepareStatement(sqlTask);
        psTask.setString(1, fileId);
        psTask.setString(2, taskType);
        psTask.executeUpdate();

        conn.commit();
        return true;
        
    } catch (Exception e) {
        e.printStackTrace();
        try { 
            if(conn != null) conn.rollback(); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
        
    } finally {
        try { 
            if(conn != null) conn.close(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    // 2. Hàm lấy danh sách Job (NHẬN 1 THAM SỐ USERID)
    public static List<Map<String, Object>> getAllJobs(long userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return list;

            // Lọc theo User ID (hoặc lấy file của khách vãng lai nếu userId = 0)
            String sql = "SELECT * FROM tasks t JOIN files f ON t.file_id = f.file_id WHERE f.user_id " + (userId > 0 ? "= ?" : "IS NULL") + " ORDER BY t.created_at DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            if (userId > 0) {
                ps.setLong(1, userId);
            }
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", rs.getString("original_name"));
                map.put("type", rs.getString("task_type"));
                map.put("status", rs.getString("status"));
                map.put("progress", rs.getInt("progress_percent"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn!=null) conn.close(); } catch (Exception e) {}
        }
        return list;
    }
}