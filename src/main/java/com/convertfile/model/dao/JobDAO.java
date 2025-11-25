package com.convertfile.model.dao;

import com.convertfile.config.DBConnect;

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
    public static String createNewJob(String originalName, String savedName, String cloudinaryUrl,
            String taskType, String publicId, long fileSize, long userId) {
        Connection conn = null;
        // Tạo UUID ngẫu nhiên cho file
        String fileId = UUID.randomUUID().toString();

        String sqlFile = "INSERT INTO files (file_id, user_id, original_name, saved_name, file_size, " +
                "file_path, public_id, input_public_id, input_format, current_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'UPLOADED')";

        // ⚠️ Phase 3: Task creation moved to TaskQueueService (to support RabbitMQ publish)
        // Task will be created by UploadServlet → TaskQueueService.addNewTask()

        try {
            conn = DBConnect.getConnection();
            if (conn == null)
                return null;

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
            psFile.setString(6, cloudinaryUrl); // URL của Cloudinary
            psFile.setString(7, publicId); // Public ID của file output (sau khi convert)
            psFile.setString(8, publicId); // Public ID của file input (ban đầu cũng là publicId)

            // Lấy định dạng file từ tên file (ví dụ: file.docx -> docx)
            String inputFormat = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
            psFile.setString(9, inputFormat);

            psFile.executeUpdate();

            // ✅ Phase 3: Task creation handled by TaskQueueService (supports RabbitMQ publish)
            // Task inserted by UploadServlet → TaskQueueService.addNewTask(fileId, taskType)

            conn.commit();
            return fileId;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;

        } finally {
            try {
                if (conn != null)
                    conn.close();
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
            conn = DBConnect.getConnection();
            if (conn == null)
                return list;

            // Lọc theo User ID
            String sql = "SELECT * FROM tasks t JOIN files f ON t.file_id = f.file_id WHERE f.user_id = ? ORDER BY t.created_at DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("fileId", rs.getString("file_id")); // ✅ Add fileId
                map.put("name", rs.getString("original_name"));
                map.put("type", rs.getString("task_type"));
                map.put("status", rs.getString("status"));
                map.put("progress", rs.getInt("progress_percent"));
                list.add(map);
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
        return list;
    }

    // 3. Hàm lấy danh sách Job theo danh sách File ID (Cho Guest)
    public static List<Map<String, Object>> getJobsByFileIds(List<String> fileIds) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (fileIds == null || fileIds.isEmpty())
            return list;

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null)
                return list;

            StringBuilder sqlBuilder = new StringBuilder(
                    "SELECT * FROM tasks t JOIN files f ON t.file_id = f.file_id WHERE f.file_id IN (");
            for (int i = 0; i < fileIds.size(); i++) {
                sqlBuilder.append("?");
                if (i < fileIds.size() - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(") ORDER BY t.created_at DESC");

            PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString());
            for (int i = 0; i < fileIds.size(); i++) {
                ps.setString(i + 1, fileIds.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("fileId", rs.getString("file_id")); // ✅ Add fileId
                map.put("name", rs.getString("original_name"));
                map.put("type", rs.getString("task_type"));
                map.put("status", rs.getString("status"));
                map.put("progress", rs.getInt("progress_percent"));
                list.add(map);
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
        return list;
    }
}

