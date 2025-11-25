package com.convertfile.test;

import com.convertfile.config.DBConnect;
import com.convertfile.service.CloudService.CloudDeleteService;
import com.convertfile.model.dao.FileDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Standalone test để xóa guest files và verify Cloudinary deletion
 * Run: java -cp ... com.convertfile.test.ManualCleanupTest
 */
public class ManualCleanupTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("🧪 MANUAL CLEANUP TEST");
        System.out.println("========================================\n");
        
        try {
            // 1. Lấy tất cả guest files
            String sql = "SELECT file_id, public_id, original_name FROM files WHERE user_id = 0";
            
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                int count = 0;
                
                while (rs.next()) {
                    count++;
                    String fileId = rs.getString("file_id");
                    String publicId = rs.getString("public_id");
                    String originalName = rs.getString("original_name");
                    
                    System.out.println("📁 File #" + count + ": " + originalName);
                    System.out.println("   ID: " + fileId);
                    System.out.println("   Public ID: " + publicId);
                    
                    // 2. Xóa trên Cloudinary
                    System.out.print("   🗑️  Deleting from Cloudinary... ");
                    boolean cloudDeleted = CloudDeleteService.deleteFile(publicId);
                    
                    if (cloudDeleted) {
                        System.out.println("✅ SUCCESS");
                        
                        // 3. Xóa trong DB
                        System.out.print("   🗑️  Deleting from database... ");
                        boolean dbDeleted = FileDAO.deleteFileById(fileId);
                        
                        if (dbDeleted) {
                            System.out.println("✅ SUCCESS");
                        } else {
                            System.out.println("❌ FAILED");
                        }
                    } else {
                        System.out.println("❌ FAILED");
                    }
                    
                    System.out.println();
                }
                
                if (count == 0) {
                    System.out.println("ℹ️  No guest files found");
                } else {
                    System.out.println("========================================");
                    System.out.println("📊 Total processed: " + count + " files");
                    System.out.println("========================================");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
