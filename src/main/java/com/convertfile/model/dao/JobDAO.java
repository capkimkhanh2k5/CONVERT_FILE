package com.convertfile.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class JobDAO {

    public static boolean createNewJob(String originalName, String savedName, String fullPath, String taskType) {
        // 1. Khai báo kết nối ở ngoài (để null trước)
        Connection conn = null;
        
        String fileId = UUID.randomUUID().toString(); 
        String sqlFile = "INSERT INTO files (file_id, original_name, saved_name, file_size, input_path, input_format) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlTask = "INSERT INTO tasks (file_id, task_type, status, progress_percent) VALUES (?, ?, 'WAITING', 0)";

        try {
            // 2. --- SỬA LỖI TẠI ĐÂY ---
            // Đưa dòng này vào trong TRY để bắt lỗi SQLException
            conn = ConnectDB.getConnection();
            
            // Nếu không kết nối được thì dừng luôn
            if (conn == null) {
                System.out.println("❌ Lỗi: Không thể kết nối Database trong JobDAO");
                return false;
            }

            // 3. Tắt chế độ tự lưu
            conn.setAutoCommit(false);

            // 4. Thêm vào bảng FILES
            PreparedStatement psFile = conn.prepareStatement(sqlFile);
            psFile.setString(1, fileId);
            psFile.setString(2, originalName);
            psFile.setString(3, savedName);
            psFile.setLong(4, 1024); 
            psFile.setString(5, fullPath);
            psFile.setString(6, "docx"); 
            psFile.executeUpdate();

            // 5. Thêm vào bảng TASKS
            PreparedStatement psTask = conn.prepareStatement(sqlTask);
            psTask.setString(1, fileId);
            psTask.setString(2, taskType);
            psTask.executeUpdate();

            // 6. Chốt đơn (Commit)
            conn.commit();
            System.out.println("✅ Đã lưu Job vào Database: " + originalName);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { 
                if (conn != null) conn.rollback(); // Nếu lỗi thì hoàn tác
            } catch (Exception ex) {} 
            return false;
        } finally {
             // Đóng kết nối (Optional nhưng nên làm)
             try { if (conn != null) conn.close(); } catch (Exception ex) {}
        }
    }

    // Hàm lấy danh sách tất cả các Job để hiển thị ra Web
    public static java.util.List<java.util.Map<String, Object>> getAllJobs() {
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        try {
            String sql = "SELECT * FROM tasks JOIN files ON tasks.file_id = files.file_id ORDER BY tasks.created_at DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("name", rs.getString("original_name"));
                map.put("type", rs.getString("task_type"));
                map.put("status", rs.getString("status"));
                map.put("progress", rs.getInt("progress_percent"));
                list.add(map);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}