package com.convertfile.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.convertfile.model.EnumStatus.FileStatus;
import com.convertfile.model.FileInfo;

public class FileDAO {
    public boolean insertFile(FileInfo file) {
        String sql = """
            INSERT INTO files (file_id, user_id, original_name, saved_name, file_size, input_path, output_path, 
            input_format, output_format, current_status, description, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, file.getFile_id());
            ps.setLong(2, file.getUser_id());
            ps.setString(3, file.getOriginal_name());
            ps.setString(4, file.getSaved_name());
            ps.setLong(5, file.getFile_size());
            ps.setString(6, file.getInput_path());
            ps.setString(7, file.getOutput_path());
            ps.setString(8, file.getInput_format());
            ps.setString(9, file.getOutput_format());
            ps.setString(10, file.getCurrent_status().name());
            ps.setString(11, file.getDescription());
            ps.setTimestamp(12, Timestamp.valueOf(file.getCreated_at()));
            ps.setTimestamp(13, Timestamp.valueOf(file.getUpdated_at()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FileInfo> getAllFiles(){
        List<FileInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM files";
        try(Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                list.add(mapRow(rs));
            }
            
        } catch (Exception e) {
            e.printStackTrace();   
        }

        return list;
    }

    public FileInfo getFileByID(String fileID){
        String sql = "SELECT * FROM files WHERE file_id = ?";
        try(Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, fileID);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean updateStatus(String fileID, String status){
        
        String sql = "UPDATE files SET current_status = ? AND updated_at = ? WHERE file_id = ?";
        try(Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            LocalDateTime now = LocalDateTime.now();

            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setString(3, fileID);

            return ps.executeUpdate() > 0;
            
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private FileInfo mapRow(ResultSet rs) throws SQLException {
        FileInfo f = new FileInfo();

        f.setFile_id(rs.getString("file_id"));
        f.setUser_id(rs.getLong("user_id"));
        f.setOriginal_name(rs.getString("original_name"));
        f.setSaved_name(rs.getString("saved_name"));
        f.setFile_size(rs.getLong("file_size"));
        f.setInput_path(rs.getString("input_path"));
        f.setOutput_path(rs.getString("output_path"));
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
}
