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

    // 1. Hàm tạo Job mới (NHẬN 5 THAM SỐ - CÓ USERID)
    public static boolean createNewJob(String originalName, String savedName, String fullPath, String taskType, long userId) {
        Connection conn = null;
        // Tạo UUID ngẫu nhiên cho file
        String fileId = UUID.randomUUID().toString(); 

        String sqlFile = "INSERT INTO files (file_id, original_name, saved_name, file_size, input_path, input_format, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlTask = "INSERT INTO tasks (file_id, task_type, status, progress_percent) VALUES (?, ?, 'WAITING', 0)";

        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false);

            // Thêm vào bảng FILES
            PreparedStatement psFile = conn.prepareStatement(sqlFile);
            psFile.setString(1, fileId);
            psFile.setString(2, originalName);
            psFile.setString(3, savedName);
            psFile.setLong(4, 1024); // Tạm để size cố định
            psFile.setString(5, fullPath);
            psFile.setString(6, "docx"); // Tạm để format
            
            // Quan trọng: Xử lý User ID
            if (userId > 0) {
                psFile.setLong(7, userId);
            } else {
                psFile.setNull(7, java.sql.Types.BIGINT);
            }
            
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
            try { if(conn!=null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            try { if(conn!=null) conn.close(); } catch (Exception e) {}
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