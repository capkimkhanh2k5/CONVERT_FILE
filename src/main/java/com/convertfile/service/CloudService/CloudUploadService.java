package com.convertfile.service.CloudService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;

public class CloudUploadService {

    /**
     * Upload file từ Part (multipart) - AUTHENTICATED MODE
     */
    public static Map<String, Object> uploadFile(Part part, String fileName, String taskType)
            throws IOException {

        Cloudinary cloudinary = CloudConnect.getInstance();
        
        // Tạo temp file trong thư mục java.io.tmpdir (có quyền ghi)
        String tmpDir = System.getProperty("java.io.tmpdir");
        File tempFile = File.createTempFile("upload_", "_" + fileName, new File(tmpDir));

        try (InputStream input = part.getInputStream()) {
            Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", "convertfile/" + taskType);
            uploadParams.put("resource_type", "raw");
            uploadParams.put("use_filename", true);
            uploadParams.put("unique_filename", true);
            uploadParams.put("type", "upload"); // ✅ PUBLIC - Simple and works!
            uploadParams.put("access_mode", "public"); // 🔓 Force public access

            System.out.println("🔧 Upload params: " + uploadParams);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(tempFile, uploadParams);
            
            System.out.println("📊 Upload response - type: " + uploadResult.get("type"));
            System.out.println("📊 Upload response - access_mode: " + uploadResult.get("access_mode"));
            
            return uploadResult;

        } finally {
            tempFile.delete();
        }
    }

    /**
     * Upload file từ File object - AUTHENTICATED MODE
     */
    public static Map<String, Object> uploadFile(File file, String fileName, String taskType)
            throws IOException {

        Cloudinary cloudinary = CloudConnect.getInstance();

        Map<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("folder", "convertfile/" + taskType);
        uploadParams.put("resource_type", "raw");
        uploadParams.put("use_filename", true);
        uploadParams.put("unique_filename", true);
        uploadParams.put("type", "upload"); // ✅ PUBLIC - Simple and works!
        uploadParams.put("access_mode", "public"); // 🔓 Force public access

        System.out.println("🔧 Upload params (File): " + uploadParams);

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file, uploadParams);
        
        System.out.println("📊 Upload response - type: " + uploadResult.get("type"));
        System.out.println("📊 Upload response - access_mode: " + uploadResult.get("access_mode"));
        
        return uploadResult;
    }

    /**
     * ✅ TẠO SIGNED URL VỚI CLOUDINARY SDK
     * @param publicId - Public ID của file trên Cloudinary (format: convertfile/PDF_TO_DOCX/filename_xxx.pdf)
     * @return Signed URL có thể download
     */
    public static String generateSignedUrl(String publicId) {
        try {
            Cloudinary cloudinary = CloudConnect.getInstance();
            
            // Tạo signed URL với SDK
            String signedUrl = cloudinary.url()
                .resourceType("raw")
                .type("upload")
                .signed(true)
                .generate(publicId);
            
            System.out.println("🔐 Generated signed URL: " + signedUrl);
            return signedUrl;
            
        } catch (Exception e) {
            System.err.println("❌ Error generating signed URL: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple URL
            return "https://res.cloudinary.com/davtsqowt/raw/upload/" + publicId;
        }
    }

    /**
     * Xóa file trên Cloudinary
     */
    public static void deleteFile(String publicId) throws Exception {
        Cloudinary cloudinary = CloudConnect.getInstance();
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
            "resource_type", "raw",
            "type", "upload"
        ));
    }
}