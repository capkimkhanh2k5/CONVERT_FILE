package com.convertfile.worker;

import com.convertfile.model.dao.FileDAO;
import com.convertfile.model.dao.TaskDAO;
import com.convertfile.model.bean.Files;
import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.service.PdfTool;
import com.convertfile.service.CloudService.CloudUploadService;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class FileWorker implements Runnable {

    private final TaskDAO taskDAO = new TaskDAO();
    private final FileDAO fileDAO = new FileDAO();

    @Override
    public void run() {
        System.out.println("🤖 WORKER (CLOUD) ĐÃ KHỞI ĐỘNG...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                processNextJob();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("⚠️ WORKER: Nhận tín hiệu shutdown...");
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Exit loop gracefully
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("🛑 WORKER: Đã dừng hoạt động.");
    }

    private void processNextJob() {
        Path tempInput = null;
        Path tempOutput = null;

        try {
            // 1. Lấy task tiếp theo từ hàng đợi (qua DAO)
            Tasks task = taskDAO.getNextWaitingTask();
            if (task == null) {
                return; // Không có task nào đang chờ
            }

            long taskId = task.getTask_id();
            String fileId = task.getFileId();
            String taskType = task.getTask_type().name();

            // 2. Lấy thông tin file (qua DAO)
            Files file = fileDAO.getFileByID(fileId);
            if (file == null) {
                System.out.println("❌ Không tìm thấy file: " + fileId);
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "File not found");
                return;
            }

            String fileUrl = file.getFile_path(); // URL Cloudinary
            String savedName = file.getSaved_name();

            System.out.println("🔥 Bắt đầu xử lý Task ID: " + taskId);
            System.out.println("   File ID: " + fileId);
            System.out.println("   URL: " + fileUrl);

            // 3. Cập nhật trạng thái sang PROCESSING (qua DAO)
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 0, "Starting conversion");

            try {
                // 4. Download file từ Cloudinary về Temp Input
                System.out.println("   ⬇️ Đang tải file từ Cloudinary...");
                tempInput = java.nio.file.Files.createTempFile("input_", "_" + savedName);
                try (InputStream in = new URL(fileUrl).openStream()) {
                    java.nio.file.Files.copy(in, tempInput, StandardCopyOption.REPLACE_EXISTING);
                }
                System.out.println("   ✅ Đã tải về Temp: " + tempInput.toString());

                // 5. Tạo Temp Output
                String outputExtension = savedName.toLowerCase().endsWith(".pdf") ? ".docx" : ".docx";
                tempOutput = java.nio.file.Files.createTempFile("output_", outputExtension);

                // 6. Xử lý Convert (PDF -> DOCX)
                System.out.println("   ⚙️ Đang convert...");

                if (savedName.toLowerCase().endsWith(".pdf")) {
                    PdfTool.convertPdfToDocx(tempInput.toString(), tempOutput.toString());
                } else {
                    // Dummy convert cho các loại khác
                    java.nio.file.Files.copy(tempInput, tempOutput, StandardCopyOption.REPLACE_EXISTING);
                }

                // 7. Cập nhật tiến độ (qua DAO)
                for (int k = 10; k <= 80; k += 20) {
                    taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, k, "Converting...");
                    Thread.sleep(200);
                }

                // 8. Upload kết quả lên Cloudinary
                System.out.println("   ☁️ Đang upload kết quả lên Cloudinary...");
                Map<String, Object> uploadResult = CloudUploadService.uploadFile(
                        tempOutput.toFile(),
                        "converted_" + savedName,
                        taskType);

                String newUrl = (String) uploadResult.get("secure_url");
                String newPublicId = (String) uploadResult.get("public_id");
                String newSavedName = (String) uploadResult.get("original_filename") + "."
                        + (String) uploadResult.get("format");
                long newSize = ((Number) uploadResult.get("bytes")).longValue();

                System.out.println("   ✅ Upload thành công: " + newUrl);

                // 9. Cập nhật thông tin file mới vào Database (qua DAO)
                fileDAO.updateConvertedFile(fileId, newUrl, newSavedName, newSize, newPublicId);

                // 10. Cập nhật trạng thái task COMPLETED (qua DAO)
                taskDAO.updateStatus(taskId, TaskStatus.COMPLETED, 100, "Conversion completed");
                System.out.println("✅ Task " + taskId + " HOÀN THÀNH!");

            } catch (Exception ex) {
                ex.printStackTrace();
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "Error: " + ex.getMessage());
                System.out.println("❌ Lỗi xử lý: " + ex.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cleanup temporary files
            try {
                if (tempInput != null)
                    java.nio.file.Files.deleteIfExists(tempInput);
                if (tempOutput != null)
                    java.nio.file.Files.deleteIfExists(tempOutput);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
