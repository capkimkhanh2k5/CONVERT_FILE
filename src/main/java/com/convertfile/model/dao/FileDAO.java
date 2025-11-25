package com.convertfile.model.dao;

import com.convertfile.config.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.convertfile.model.bean.Files;
import com.convertfile.model.bean.EnumStatus.FileStatus;

public class FileDAO {
    public boolean insertFile(Files file) {
        String sql = """
                INSERT INTO files (file_id, user_id, original_name, saved_name, file_size, file_path,
                public_id, input_public_id, input_format, output_format, current_status, description, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, file.getFile_id());
            ps.setLong(2, file.getUser_id());
            ps.setString(3, file.getOriginal_name());
            ps.setString(4, file.getSaved_name());
            ps.setLong(5, file.getFile_size());
            ps.setString(6, file.getFile_path());
            ps.setString(7, file.getPublic_id());
            ps.setString(8, file.getInput_public_id());
            ps.setString(9, file.getInput_format());
            ps.setString(10, file.getOutput_format());
            ps.setString(11, file.getCurrent_status().name());
            ps.setString(12, file.getDescription());
            ps.setTimestamp(13, Timestamp.valueOf(file.getCreated_at()));
            ps.setTimestamp(14, Timestamp.valueOf(file.getUpdated_at()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Files> getAllFiles() {
        List<Files> list = new ArrayList<>();
        String sql = "SELECT * FROM files";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Files getFileByID(String fileID) {
        String sql = "SELECT * FROM files WHERE file_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fileID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateStatus(String fileID, String status) {

        String sql = "UPDATE files SET current_status = ?, updated_at = ? WHERE file_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();

            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setString(3, fileID);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Files mapRow(ResultSet rs) throws SQLException {
        Files f = new Files();

        f.setFile_id(rs.getString("file_id"));
        f.setUser_id(rs.getLong("user_id"));
        f.setOriginal_name(rs.getString("original_name"));
        f.setSaved_name(rs.getString("saved_name"));
        f.setFile_size(rs.getLong("file_size"));
        f.setFile_path(rs.getString("file_path"));
        f.setPublic_id(rs.getString("public_id"));
        f.setInput_public_id(rs.getString("input_public_id")); // Public ID của file gốc
        f.setInput_format(rs.getString("input_format"));
        f.setOutput_format(rs.getString("output_format"));

        String status = rs.getString("current_status");
        if (status != null) {
            f.setCurrent_status(FileStatus.valueOf(status));
        }

        f.setDescription(rs.getString("description"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            f.setCreated_at(created.toLocalDateTime());
        }

        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            f.setUpdated_at(updated.toLocalDateTime());
        }

        return f;
    }

    public String[] getAllFile_idsByUser_id(long user_id) {
        String sql = "SELECT file_id FROM files WHERE user_id = ?";
        List<String> fileIds = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user_id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    fileIds.add(rs.getString("file_id"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileIds.toArray(new String[0]);
    }

    public boolean updateConvertedFile(String fileId, String filePath, String savedName, long fileSize,
            String publicId) {
        String sql = """
                UPDATE files
                SET file_path = ?, saved_name = ?, file_size = ?, public_id = ?, current_status = 'CONVERTED', updated_at = ?
                WHERE file_id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, filePath);
            ps.setString(2, savedName);
            ps.setLong(3, fileSize);
            ps.setString(4, publicId);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, fileId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy public_id của file theo file_id
     * @param fileId
     * @return public_id hoặc null nếu không tìm thấy
     */
    public static String getPublicIdByFileId(String fileId) {
        String sql = "SELECT public_id FROM files WHERE file_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fileId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("public_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting public_id for file: " + fileId);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy input_public_id (public ID của file gốc) từ DB theo file_id
     */
    public static String getInputPublicIdByFileId(String fileId) {
        String sql = "SELECT input_public_id FROM files WHERE file_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fileId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("input_public_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting input_public_id for file: " + fileId);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Xóa file theo file_id
     * @param fileId
     * @return true nếu xóa thành công
     */
    public static boolean deleteFileById(String fileId) {
        String sql = "DELETE FROM files WHERE file_id = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fileId);
            int deleted = ps.executeUpdate();
            return deleted > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting file: " + fileId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách files đã expired (user_id > 0 và created_at > retentionHours)
     * @param retentionHours - Số giờ giữ file (ví dụ: 24)
     * @return Danh sách Map chứa {file_id, public_id, user_id, original_name}
     */
    public static List<java.util.Map<String, Object>> getExpiredFiles(long retentionHours) {
        List<java.util.Map<String, Object>> results = new ArrayList<>();
        
        String sql = """
                SELECT file_id, public_id, user_id, original_name, created_at
                FROM files
                WHERE user_id > 0 
                  AND created_at < DATE_SUB(NOW(), INTERVAL ? HOUR)
                ORDER BY created_at ASC
                """;

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, retentionHours);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> fileInfo = new java.util.HashMap<>();
                    fileInfo.put("file_id", rs.getString("file_id"));
                    fileInfo.put("public_id", rs.getString("public_id"));
                    fileInfo.put("user_id", rs.getLong("user_id"));
                    fileInfo.put("original_name", rs.getString("original_name"));
                    fileInfo.put("created_at", rs.getTimestamp("created_at"));
                    results.add(fileInfo);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting expired files");
            e.printStackTrace();
        }

        return results;
    }
}

