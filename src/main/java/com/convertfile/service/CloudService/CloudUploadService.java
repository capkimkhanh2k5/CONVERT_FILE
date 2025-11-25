package com.convertfile.service.CloudService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import com.cloudinary.Cloudinary;

import jakarta.servlet.http.Part;

public class CloudUploadService {

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000; // 1 second

    /**
     * Execute operation with retry logic (exponential backoff)
     */
    private static <T> T executeWithRetry(CloudinaryOperation<T> operation) throws IOException {
        int attempt = 0;
        long backoffMs = INITIAL_BACKOFF_MS;

        while (true) {
            try {
                return operation.execute();
            } catch (IOException e) {
                attempt++;
                
                // Check if error is retryable
                boolean isRetryable = isRetryableError(e);
                
                if (!isRetryable || attempt >= MAX_RETRIES) {
                    System.err.println("❌ Cloudinary operation failed after " + attempt + " attempts");
                    throw e;
                }

                System.err.println("⚠️ Cloudinary error (attempt " + attempt + "/" + MAX_RETRIES + "): " + e.getMessage());
                System.err.println("⏳ Retrying in " + backoffMs + "ms...");

                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Retry interrupted", ie);
                }

                backoffMs *= 2; // Exponential backoff: 1s, 2s, 4s
            }
        }
    }

    /**
     * Check if error is retryable (network errors, rate limits)
     */
    private static boolean isRetryableError(IOException e) {
        String msg = e.getMessage();
        if (msg == null) return false;

        // Retry on network errors, timeouts, rate limits
        return msg.contains("timeout") || 
               msg.contains("429") || // Rate limit
               msg.contains("503") || // Service unavailable
               msg.contains("Connection refused") ||
               msg.contains("Connection reset");
    }

    @FunctionalInterface
    private interface CloudinaryOperation<T> {
        T execute() throws IOException;
    }

    /**
     * Upload file từ Part (multipart) - AUTHENTICATED MODE (with retry)
     */
    public static Map<String, Object> uploadFile(Part part, String fileName, String taskType)
            throws IOException {

        // Tạo temp file trong thư mục java.io.tmpdir (có quyền ghi)
        String tmpDir = System.getProperty("java.io.tmpdir");
        File tempFile = File.createTempFile("upload_", "_" + fileName, new File(tmpDir));

        try (InputStream input = part.getInputStream()) {
            Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            final File finalTempFile = tempFile;
            
            // Execute upload with retry logic
            return executeWithRetry(() -> {
                Cloudinary cloudinary = CloudConnect.getInstance();
                
                Map<String, Object> uploadParams = new HashMap<>();
                uploadParams.put("folder", "convertfile/" + taskType);
                uploadParams.put("resource_type", "raw");
                uploadParams.put("use_filename", true);
                uploadParams.put("unique_filename", true);
                uploadParams.put("type", "upload"); // ✅ PUBLIC - Simple and works!
                uploadParams.put("access_mode", "public"); // 🔓 Force public access

                System.out.println("🔧 Upload params: " + uploadParams);

                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = cloudinary.uploader().upload(finalTempFile, uploadParams);
                
                System.out.println("📊 Upload response - type: " + uploadResult.get("type"));
                System.out.println("📊 Upload response - access_mode: " + uploadResult.get("access_mode"));
                
                return uploadResult;
            });

        } finally {
            tempFile.delete();
        }
    }

    /**
     * Upload file từ File object - AUTHENTICATED MODE (with retry)
     */
    public static Map<String, Object> uploadFile(File file, String fileName, String taskType)
            throws IOException {

        return executeWithRetry(() -> {
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
        });
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

}
