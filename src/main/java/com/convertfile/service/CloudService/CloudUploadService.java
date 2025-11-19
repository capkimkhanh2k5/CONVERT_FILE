package com.convertfile.service.CloudService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;

public class CloudUploadService {
    
    /**
     * Upload file từ Part (multipart) lên Cloudinary
     * @param part - file từ request
     * @param fileName - tên file gốc
     * @param taskType - loại task (để tạo folder)
     * @return Map chứa thông tin file đã upload (url, public_id, ...)
     */
    public static Map<String, Object> uploadFile(Part part, String fileName, String taskType) 
            throws IOException {
        
        Cloudinary cloudinary = CloudConnect.getInstance();
        
        // Tạo file tạm để upload (vì Cloudinary cần File object)
        File tempFile = File.createTempFile("upload_", "_" + fileName);
        
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            // Upload lên Cloudinary với options
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                "folder", "convertfile/" + taskType, // Tạo folder theo taskType
                "resource_type", "auto", // Tự động detect loại file
                "use_filename", true,
                "unique_filename", true
            ));
            
            return uploadResult;
            
        } finally {
            // Xóa file tạm
            tempFile.delete();
        }
    }
    
    /**
     * Xóa file trên Cloudinary
     * @param publicId - ID của file trên Cloudinary
     */
    public static void deleteFile(String publicId) throws Exception {
        Cloudinary cloudinary = CloudConnect.getInstance();
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}