package com.convertfile.scheduler;

import com.convertfile.model.dao.CloudinaryFileTracker;
import com.convertfile.model.dao.FileDAO;
import com.convertfile.service.CloudService.CloudDeleteService;
import java.util.List;
import java.util.Map;

/**
 * Scheduled job để xóa files của user đã đăng nhập sau 24h
 * Chạy trong background thread riêng
 * Note: tasks tự động xóa theo nhờ ON DELETE CASCADE
 */
public class FileCleanupScheduler implements Runnable {

    private volatile boolean running = true;
    private static final long CLEANUP_INTERVAL = 6 * 60 * 60 * 1000; // 6 giờ
    private static final long FILE_RETENTION_HOURS = 24; // Files của user lưu 24h

    @Override
    public void run() {
        System.out.println("🧹 FileCleanupScheduler started! Will run every 6 hours.");
        
        while (running) {
            try {
                // Chờ 6 giờ trước khi chạy lần đầu (hoặc lần tiếp theo)
                Thread.sleep(CLEANUP_INTERVAL);
                
                if (!running) {
                    break;
                }
                
                System.out.println("\n========================================");
                System.out.println("🧹 Running scheduled file cleanup...");
                System.out.println("========================================");
                
                cleanupExpiredFiles();
                
                System.out.println("========================================");
                System.out.println("🧹 Scheduled cleanup completed");
                System.out.println("========================================\n");
                
            } catch (InterruptedException e) {
                System.out.println("⚠️ FileCleanupScheduler interrupted, shutting down...");
                running = false;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("❌ Error in FileCleanupScheduler: " + e.getMessage());
                e.printStackTrace();
                // Tiếp tục chạy dù có lỗi
            }
        }
        
        System.out.println("🛑 FileCleanupScheduler stopped.");
    }

    /**
     * Xóa các files đã hết hạn (> 24h) của user có tài khoản
     */
    private void cleanupExpiredFiles() {
        try {
            // Lấy danh sách files đã expired từ database
            List<Map<String, Object>> expiredFiles = FileDAO.getExpiredFiles(FILE_RETENTION_HOURS);
            
            if (expiredFiles.isEmpty()) {
                System.out.println("ℹ️ No expired files found");
                return;
            }
            
            System.out.println("📋 Found " + expiredFiles.size() + " expired files to clean up");
            
            int deletedCount = 0;
            int failedCount = 0;
            
            for (Map<String, Object> fileInfo : expiredFiles) {
                String fileId = (String) fileInfo.get("file_id");
                String publicId = (String) fileInfo.get("public_id");
                Long userId = (Long) fileInfo.get("user_id");
                String originalName = (String) fileInfo.get("original_name");
                
                try {
                    System.out.println("   Processing: " + fileId + " (User: " + userId + ", File: " + originalName + ")");
                    
                    // 1. Lấy TẤT CẢ public_ids (cả input và output files)
                    List<String> publicIds = CloudinaryFileTracker.getAllPublicIdsForFile(fileId);
                    
                    // 2. Xóa TẤT CẢ files từ Cloudinary
                    int cloudDeleteCount = CloudDeleteService.deleteMultipleFiles(publicIds);
                    
                    if (cloudDeleteCount > 0) {
                        // 3. Xóa từ database (tasks tự xóa nhờ ON DELETE CASCADE)
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
            
            System.out.println("📊 Cleanup summary:");
            System.out.println("   ✅ Deleted: " + deletedCount);
            System.out.println("   ❌ Failed: " + failedCount);
            
        } catch (Exception e) {
            System.err.println("❌ Error in cleanupExpiredFiles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dừng scheduler
     */
    public void stop() {
        System.out.println("🛑 Stopping FileCleanupScheduler...");
        running = false;
    }
}
