package com.convertfile.worker;

import com.convertfile.model.dao.FileDAO;
import com.convertfile.model.dao.TaskDAO;
import com.convertfile.model.bean.Files;
import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.bean.EnumStatus.TaskType;
import com.convertfile.service.ConvertService.PdfTool;
import com.convertfile.service.CloudService.CloudUploadService;
import com.convertfile.service.ConvertService.docx_to_pdf_service;
import com.convertfile.service.ConvertService.csv_to_json_service;

import java.io.InputStream;
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
        long taskId = 0;

        try {
            // 1. Lấy task từ hàng đợi
            Tasks task = taskDAO.getNextWaitingTask();
            if (task == null) return;

            taskId = task.getTask_id();
            String fileId = task.getFileId();
            
            // Chuyển String sang Enum (Cần đảm bảo tên trong DB khớp với Enum)
            TaskType typeEnum;
            try {
                // Nếu DB lưu "DOCX_TO_PDF" thì Enum cũng phải là DOCX_TO_PDF
                typeEnum = task.getTask_type(); 
            } catch (Exception e) {
                System.err.println("❌ Lỗi: Loại Task không hợp lệ trong DB: " + task.getTask_type());
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "Invalid Task Type");
                return;
            }

            // 2. Lấy thông tin file
            Files file = fileDAO.getFileByID(fileId);
            if (file == null) {
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "File not found in DB");
                return;
            }

            String publicId = file.getPublic_id(); // Lấy public_id từ DB
            String savedName = file.getSaved_name();

            System.out.println("🔥 Xử lý Task [" + taskId + "] Type: " + typeEnum);
            System.out.println("   📋 Public ID from DB: " + publicId);
            System.out.println("   📄 Saved Name: " + savedName);

            // 3. Mark Processing
            taskDAO.markTaskProcessing(taskId, "Worker-Main");

            // 4. ✅ TẠO SIGNED URL (HẾT HẠN SAU 1 GIỜ)
            System.out.println("   🔐 Getting download URL...");
            
            // 4. XÁC ĐỊNH URL ĐỂ DOWNLOAD
            // ✅ LUÔN DÙNG file_path TỪ DATABASE (URL gốc từ Cloudinary)
            String filePath = file.getFile_path();
            String downloadUrl = null;
            
            if (filePath != null && !filePath.trim().isEmpty() && filePath.startsWith("http")) {
                // Dùng URL gốc từ database (chính xác nhất!)
                downloadUrl = filePath;
                System.out.println("   ✅ Using file_path from database");
                System.out.println("   🔗 URL: " + downloadUrl);
            } else if (publicId != null && !publicId.trim().isEmpty()) {
                // Fallback: Generate từ public_id nếu file_path không có
                System.err.println("⚠️ WARNING: file_path is empty, trying public_id...");
                downloadUrl = CloudUploadService.generateSignedUrl(publicId);
                System.out.println("   🔗 Generated URL from public_id: " + downloadUrl);
            } else {
                throw new Exception("Both file_path and public_id are invalid! filePath=" + filePath + ", publicId=" + publicId);
            }
            
            // 5. DOWNLOAD FILE
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 20, "Downloading file...");
            System.out.println("   ⬇️ Downloading from URL...");
            tempInput = java.nio.file.Files.createTempFile("input_", "_" + savedName);
            
            try {
                try (InputStream in = new java.net.URI(downloadUrl).toURL().openStream()) {
                    java.nio.file.Files.copy(in, tempInput, StandardCopyOption.REPLACE_EXISTING);
                }
                System.out.println("   ✅ Download completed!");
                taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 25, "Download completed");
            } catch (java.io.IOException e) {
                if (e.getMessage().contains("401")) {
                    System.err.println("   ❌ 401 Unauthorized - File is private/authenticated");
                    System.err.println("   💡 This is an OLD file - SKIPPING this task");
                    System.err.println("   🔗 URL: " + downloadUrl);
                    
                    // ✅ ĐÁNH DẤU TASK NÀY LÀ FAILED và KHÔNG THỬ LẠI
                    taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, 
                        "Old private file - cannot download. Please delete and re-upload.");
                    
                    System.err.println("   ⏭️ Skipping to next task...");
                    return; // Exit processNextJob() để xử lý task tiếp theo
                } else {
                    throw e;
                }
            }

            // 5. Xác định đuôi file đầu ra & Tạo Temp Output
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 30, "Preparing output...");
            String outputExt = getOutputExtension(typeEnum);
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 35, "Creating temp file...");
            tempOutput = java.nio.file.Files.createTempFile("output_", outputExt);

            // 6. --- CORE CONVERT LOGIC (SWITCH-CASE) ---
            Thread.sleep(300);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 40, "Starting conversion...");
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 45, "Converting file...");
            System.out.println("   ⚙️ Converting (" + typeEnum + ")...");
            Thread.sleep(300);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 55, "Processing conversion...");
            
            switch (typeEnum) {
                case PDF_TO_DOCX:
                    PdfTool.convertPdfToDocx(tempInput.toString(), tempOutput.toString());
                    break;

                case DOCX_TO_PDF:
                    // Gọi service DOCX -> PDF
                    docx_to_pdf_service docxService = new docx_to_pdf_service();
                    docxService.convertDocxtoPdf(tempInput.toString(), tempOutput.toString());
                    break;
                
                case CSV_TO_JSON:
                    csv_to_json_service csvService = new csv_to_json_service();
                    csvService.convertCsvToJson(tempInput.toString(), tempOutput.toString());
                    break;
                
                // TODO: Enable these conversions when dependencies are configured
                /*
                case DOCX_TO_XML:
                    docx_to_xml_service docxToXmlService = new docx_to_xml_service();
                    docxToXmlService.convertDocxToXml(tempInput.toString(), tempOutput.toString());
                    break;
                
                case XML_TO_DOCX:
                    xml_to_docx_service xmlToDocxService = new xml_to_docx_service();
                    xmlToDocxService.convertXmlToDocx(tempInput.toString(), tempOutput.toString());
                    break;
                
                case DOCX_TO_HTML:
                    docx_to_html_service docxToHtmlService = new docx_to_html_service();
                    docxToHtmlService.convertDocxtoHtml(tempInput.toString(), tempOutput.toString());
                    break;
                
                case DOCX_TO_TXT:
                    docx_to_txt_service docxToTxtService = new docx_to_txt_service();
                    docxToTxtService.convertDocxToTxt(tempInput.toString(), tempOutput.toString());
                    break;
                
                case DOCX_TO_MARKDOWN:
                    docx_to_markdown_service docxToMdService = new docx_to_markdown_service();
                    docxToMdService.convertDocxToMarkdown(tempInput.toString(), tempOutput.toString());
                    break;
                
                case IMAGE_TO_PDF:
                    image_to_pdf_service imgToPdfService = new image_to_pdf_service();
                    imgToPdfService.convertImageToPdf(tempInput.toString(), tempOutput.toString());
                    break;
                
                case PDF_TO_IMAGE:
                    pdf_to_image_service pdfToImgService = new pdf_to_image_service();
                    pdfToImgService.convertPdfToImage(tempInput.toString(), tempOutput.toString());
                    break;
                
                case XLSX_TO_CSV:
                    xlsx_to_csv_service xlsxToCsvService = new xlsx_to_csv_service();
                    xlsxToCsvService.convertXlsxToCsv(tempInput.toString(), tempOutput.toString());
                    break;
                */

                default:
                    throw new UnsupportedOperationException("Chưa hỗ trợ kiểu convert: " + typeEnum);
            }

            // Cập nhật tiến độ sau khi convert xong
            Thread.sleep(300);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 60, "Conversion completed");
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 65, "Preparing upload...");
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 70, "Uploading result...");

            // 7. Upload Output lên Cloudinary
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 75, "Uploading to cloud...");
            System.out.println("   ☁️ Uploading result...");
            // Lưu vào folder theo loại task để dễ quản lý trên Cloud
            Map<String, Object> uploadResult = CloudUploadService.uploadFile(
                    tempOutput.toFile(),
                    "result_" + savedName, // Tên file trên cloud
                    typeEnum.name()        // Tên folder
            );
            Thread.sleep(300);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 85, "Upload completed");

            String newPublicId = (String) uploadResult.get("public_id");
            
            // Lấy format từ Cloudinary, nếu null thì dùng extension từ task type
            String format = (String) uploadResult.get("format");
            String originalBaseName = savedName.contains(".") ? savedName.substring(0, savedName.lastIndexOf('.')) : savedName;
            
            // Fix: Nếu Cloudinary trả về format null (như JSON), dùng extension từ output type
            if (format == null || "null".equals(format)) {
                String ext = getOutputExtension(typeEnum);
                format = ext.startsWith(".") ? ext.substring(1) : ext;
                System.out.println("⚠️ Format null từ Cloudinary, dùng: " + format);
            }
            
            String newSavedName = originalBaseName + "." + format;
            
            long newSize = ((Number) uploadResult.get("bytes")).longValue();

            // 8. ✅ Cập nhật DB - Lưu public_id vào file_path
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 90, "Saving to database...");
            fileDAO.updateConvertedFile(fileId, newPublicId, newSavedName, newSize, newPublicId);
            Thread.sleep(300);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 95, "Finalizing...");
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.COMPLETED, 100, "Done");

            System.out.println("✅ Task " + taskId + " HOÀN THÀNH! Public ID: " + newPublicId);

        } catch (Exception ex) {
            ex.printStackTrace();
            if (taskId > 0) {
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "Error: " + ex.getMessage());
            }
        } finally {
            // 9. Cleanup (Quan trọng)
            try {
                if (tempInput != null) java.nio.file.Files.deleteIfExists(tempInput);
                if (tempOutput != null) java.nio.file.Files.deleteIfExists(tempOutput);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Helper: Xác định đuôi file dựa trên loại Task
    private String getOutputExtension(TaskType type) {
        switch (type) {
            case DOCX_TO_PDF:
            case IMAGE_TO_PDF:
                return ".pdf";
                
            case PDF_TO_DOCX:
            case XML_TO_DOCX:
                return ".docx";
            
            case CSV_TO_JSON:
                return ".json";
            
            case DOCX_TO_XML:
                return ".xml";
            
            case DOCX_TO_HTML:
                return ".html";
            
            case DOCX_TO_TXT:
                return ".txt";
            
            case DOCX_TO_MARKDOWN:
                return ".md";
            
            case PDF_TO_IMAGE:
                return ".png";
            
            case XLSX_TO_CSV:
                return ".csv";

            default:
                return ".bin"; // Fallback
        }
    }
    
}
