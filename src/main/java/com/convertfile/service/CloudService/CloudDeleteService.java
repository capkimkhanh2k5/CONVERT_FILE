package com.convertfile.service.CloudService;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;

/**
 * Service để xóa files trên Cloudinary
 */
public class CloudDeleteService {

    /**
     * Xóa file trên Cloudinary bằng public_id
     * @param publicId - Public ID của file (format: convertfile/PDF_TO_DOCX/filename_xxx.pdf)
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public static boolean deleteFile(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            System.err.println("❌ Cannot delete file: publicId is null or empty");
            return false;
        }

        try {
            Cloudinary cloudinary = CloudConnect.getInstance();
            
            System.out.println("🗑️ Attempting to delete file from Cloudinary: " + publicId);
            
            // Xóa file với resource_type = raw (vì ta upload files không phải image)
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, 
                ObjectUtils.asMap(
                    "resource_type", "raw",
                    "type", "upload",
                    "invalidate", true  // Xóa cached version
                )
            );
            
            String resultStatus = (String) result.get("result");
            
            if ("ok".equals(resultStatus) || "not found".equals(resultStatus)) {
                System.out.println("✅ File deleted successfully: " + publicId + " (Status: " + resultStatus + ")");
                return true;
            } else {
                System.err.println("⚠️ Unexpected delete result: " + resultStatus + " for " + publicId);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error deleting file from Cloudinary: " + publicId);
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa nhiều files cùng lúc
     * @param publicIds - Danh sách public IDs
     * @return Số lượng files xóa thành công
     */
    public static int deleteMultipleFiles(java.util.List<String> publicIds) {
        if (publicIds == null || publicIds.isEmpty()) {
            System.out.println("ℹ️ No files to delete");
            return 0;
        }

        int successCount = 0;
        int failCount = 0;

        System.out.println("🗑️ Starting batch delete for " + publicIds.size() + " files...");
        
        for (String publicId : publicIds) {
            if (deleteFile(publicId)) {
                successCount++;
            } else {
                failCount++;
            }
        }

        System.out.println("📊 Batch delete completed: " + successCount + " success, " + failCount + " failed");
        return successCount;
    }
}
