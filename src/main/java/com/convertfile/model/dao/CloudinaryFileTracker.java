package com.convertfile.model.dao;

import com.convertfile.config.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class để lấy tất cả public_ids liên quan đến 1 file
 * (Cả input file và output file nếu có)
 */
public class CloudinaryFileTracker {
    
    /**
     * Lấy tất cả public_ids cần xóa cho 1 file
     * Strategy: Đọc cả public_id (output) và input_public_id (input) từ DB
     * 
     * @param fileId
     * @return List of public_ids to delete
     */
    public static List<String> getAllPublicIdsForFile(String fileId) {
        List<String> publicIds = new ArrayList<>();
        
        // Query để lấy cả public_id (output) và input_public_id (input)
        String sql = """
                SELECT public_id, input_public_id
                FROM files
                WHERE file_id = ?
                """;
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, fileId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String outputPublicId = rs.getString("public_id");
                    String inputPublicId = rs.getString("input_public_id");
                    
                    // 1. Output file (file sau convert)
                    if (outputPublicId != null && !outputPublicId.trim().isEmpty()) {
                        publicIds.add(outputPublicId);
                        System.out.println("   📎 Output file: " + outputPublicId);
                    }
                    
                    // 2. Input file (file gốc)
                    if (inputPublicId != null && !inputPublicId.trim().isEmpty()) {
                        // Chỉ thêm nếu khác với output (tránh trùng)
                        if (!inputPublicId.equals(outputPublicId)) {
                            publicIds.add(inputPublicId);
                            System.out.println("   📎 Input file: " + inputPublicId);
                        } else {
                            System.out.println("   ℹ️ Input = Output (chưa convert hoặc file đã convert xong)");
                        }
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting public_ids for file: " + fileId);
            e.printStackTrace();
        }
        
        return publicIds;
    }
}

