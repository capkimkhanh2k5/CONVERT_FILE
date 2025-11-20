package com.convertfile.model.dao;

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
                input_format, output_format, current_status, description, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, file.getFile_id());
            ps.setLong(2, file.getUser_id());
            ps.setString(3, file.getOriginal_name());
            ps.setString(4, file.getSaved_name());
            ps.setLong(5, file.getFile_size());
            ps.setString(6, file.getFile_path());
            ps.setString(7, file.getInput_format());
            ps.setString(8, file.getOutput_format());
            ps.setString(9, file.getCurrent_status().name());
            ps.setString(10, file.getDescription());
            ps.setTimestamp(11, Timestamp.valueOf(file.getCreated_at()));
            ps.setTimestamp(12, Timestamp.valueOf(file.getUpdated_at()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Files> getAllFiles() {
        List<Files> list = new ArrayList<>();
        String sql = "SELECT * FROM files";
        try (Connection conn = ConnectDB.getConnection();
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
        try (Connection conn = ConnectDB.getConnection();
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

        String sql = "UPDATE files SET current_status = ? AND updated_at = ? WHERE file_id = ?";
        try (Connection conn = ConnectDB.getConnection();
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

        try (Connection conn = ConnectDB.getConnection();
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

        try (Connection conn = ConnectDB.getConnection();
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
}
