package com.convertfile.listener;

import com.convertfile.model.dao.CloudinaryFileTracker;
import com.convertfile.model.dao.FileDAO;
import com.convertfile.service.CloudService.CloudDeleteService;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.List;

/**
 * Listener để xóa files của guest user khi session hết hạn
 * Note: tasks tự động xóa theo nhờ ON DELETE CASCADE
 */
@WebListener
public class SessionCleanupListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        System.out.println("🆕 Session created: " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        System.out.println("🔥 Session destroyed: " + session.getId());
        
        // Lấy danh sách fileIds của guest từ session
        @SuppressWarnings("unchecked")
        List<String> guestFileIds = (List<String>) session.getAttribute("guestFile_ids");
        
        if (guestFileIds == null || guestFileIds.isEmpty()) {
            System.out.println("ℹ️ No guest files to clean up for session: " + session.getId());
            return;
        }
        
        System.out.println("🗑️ Cleaning up " + guestFileIds.size() + " guest files from session: " + session.getId());
        
        int deletedCount = 0;
        int failedCount = 0;
        
        for (String fileId : guestFileIds) {
            try {
                System.out.println("   Processing file: " + fileId);
                
                // 1. Lấy TẤT CẢ public_ids (cả input và output files)
                List<String> publicIds = CloudinaryFileTracker.getAllPublicIdsForFile(fileId);
                
                if (publicIds.isEmpty()) {
                    System.err.println("   ⚠️ No public_id found for file: " + fileId);
                    failedCount++;
                    continue;
                }
                
                // 2. Xóa TẤT CẢ files trên Cloudinary
                int cloudDeleteCount = CloudDeleteService.deleteMultipleFiles(publicIds);
                
                if (cloudDeleteCount > 0) {
                    // 3. Xóa file record trong database (tasks tự xóa nhờ ON DELETE CASCADE)
                    boolean dbDeleted = FileDAO.deleteFileById(fileId);
                    
                    if (dbDeleted) {
                        System.out.println("   ✅ File deleted successfully: " + fileId + " (" + cloudDeleteCount + " cloud files)");
                        deletedCount++;
                    } else {
                        System.err.println("   ⚠️ Failed to delete from database: " + fileId);
                        failedCount++;
                    }
                } else {
                    System.err.println("   ⚠️ Failed to delete from Cloudinary: " + fileId);
                    failedCount++;
                }
                
            } catch (Exception e) {
                System.err.println("   ❌ Error deleting file " + fileId + ": " + e.getMessage());
                e.printStackTrace();
                failedCount++;
            }
        }
        
        System.out.println("📊 Guest files cleanup summary for session " + session.getId() + ":");
        System.out.println("   ✅ Deleted: " + deletedCount);
        System.out.println("   ❌ Failed: " + failedCount);
    }
}
