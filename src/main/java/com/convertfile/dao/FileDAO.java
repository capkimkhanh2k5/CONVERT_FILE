package com.convertfile.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.convertfile.model.FileInfo;

public class FileDAO {
    //TODO: FIX NHÁAAAAA
    public boolean insertFileMetaData(FileInfo file) throws SQLException {
        String sql = "insert...";

        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, file.getFileId());
            ps.setString(2, file.getOriginalName());
            ps.setString(3, file.getSavedName());
            ps.setDouble(4, file.getFileSize());
            ps.setString(5, file.getStatus());
            ps.setString(6, file.getFilePath());

            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<FileInfo> getAllFiles(){
        List<FileInfo> list = new ArrayList<>();
        String sql = "select ...";
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
        String sql = "select ...";
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
        
        String sql = "update...";
        try(Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, status);
            ps.setString(2, fileID);

            return ps.executeUpdate() > 0;
            
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private FileInfo mapRow(ResultSet rs) throws SQLException {
        FileInfo f = new FileInfo();

        f.setFileId(rs.getString("file_id"));
        f.setOriginalName(rs.getString("original_name"));
        f.setSavedName(rs.getString("saved_name"));
        f.setFileSize(rs.getDouble("file_size"));
        f.setStatus(rs.getString("status"));
        f.setFilePath(rs.getString("file_path"));
        Timestamp ts = rs.getTimestamp("upload_time");

        if (ts != null) f.setUploadTime(ts.toLocalDateTime());
        return f;
    }
}
