package com.convertfile.worker;

import com.convertfile.model.dao.FileDAO;
import com.convertfile.model.dao.TaskDAO;
import com.convertfile.model.bean.Files;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.bean.EnumStatus.TaskType;
import com.convertfile.service.PdfTool;
import com.convertfile.service.FileService;
import com.convertfile.service.CloudService.CloudUploadService;
import com.convertfile.service.ConvertService.docx_to_pdf_service;
import com.convertfile.service.ConvertService.csv_to_json_service;
import com.convertfile.service.ConvertService.docx_to_xml_service;
import com.convertfile.service.ConvertService.xml_to_docx_service;
import com.convertfile.service.ConvertService.docx_to_html_service;
import com.convertfile.service.ConvertService.docx_to_txt_service;
import com.convertfile.service.ConvertService.docx_to_markdown_service;
import com.convertfile.service.ConvertService.html_to_markdown_service;
import com.convertfile.service.ConvertService.markdown_to_html_service;
import com.convertfile.service.ConvertService.pptx_to_pdf_service;
import com.convertfile.service.ConvertService.image_to_pdf_service;
import com.convertfile.service.ConvertService.image_format_service;
import com.convertfile.service.ConvertService.pdf_to_image_service;
import com.convertfile.service.ConvertService.xlsx_to_csv_service;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileWorker implements Runnable {

    private final TaskDAO taskDAO = new TaskDAO();
    private final FileDAO fileDAO = new FileDAO();
    private final String workerId;

    private static final long MIN_SLEEP_MS = 2000;  // 2 seconds
    private static final long MAX_SLEEP_MS = 10000; // 10 seconds
    private long currentSleepMs = MIN_SLEEP_MS;

    /**
     * Constructor for Phase 2 worker pool
     * @param workerId Unique identifier cho worker này (e.g., "Worker-1")
     */
    public FileWorker(String workerId) {
        this.workerId = workerId;
    }

    /**
     * Phase 2: Process single task from DB polling (called by WorkerPoolManager)
     * Không có loop - chỉ xử lý 1 task rồi return
     */
    public void processOneTask() {
        try {
            processNextJob();
        } catch (Exception e) {
            System.err.println("❌ " + workerId + " error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Phase 3: Process specific task from RabbitMQ
     * @param taskId Task ID received from RabbitMQ queue
     */
    public void processTask(long taskId) {
        try {
            processJobById(taskId);
        } catch (Exception e) {
            System.err.println("❌ " + workerId + " failed to process task " + taskId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Phase 1 legacy method (deprecated)
     * Keep for backwards compatibility
     */
    @Override
    public void run() {
        System.out.println("🤖 " + workerId + " (CLOUD) ĐÃ KHỞI ĐỘNG...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                boolean taskProcessed = processNextJob();
                
                // Exponential backoff: Increase sleep when idle, reset when busy
                if (taskProcessed) {
                    currentSleepMs = MIN_SLEEP_MS; // Reset to 2s when task found
                } else {
                    // Gradually increase to max 10s when no tasks
                    currentSleepMs = Math.min(currentSleepMs + 1000, MAX_SLEEP_MS);
                }
                
                Thread.sleep(currentSleepMs);
            } catch (InterruptedException e) {
                System.out.println("⚠️ " + workerId + ": Nhận tín hiệu shutdown...");
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Exit loop gracefully
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("🛑 " + workerId + ": Đã dừng hoạt động.");
    }

    private boolean processNextJob() {
        Path tempInput = null;
        Path tempOutput = null;

        try {
            // 1. Lấy task từ hàng đợi
            Tasks task = taskDAO.getNextWaitingTask();
            if (task == null) return false; // No task found

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
                return false;
            }

            // 2. Lấy thông tin file
            Files file = fileDAO.getFileByID(fileId);
            if (file == null) {
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "File not found in DB");
                return false;
            }

            String publicId = file.getPublic_id();
            String savedName = file.getSaved_name();
            String originalName = file.getOriginal_name();
            
            // Lấy extension từ original_name để temp file có đúng định dạng
            String inputExtension = "";
            if (originalName != null && originalName.contains(".")) {
                inputExtension = originalName.substring(originalName.lastIndexOf("."));
            }

            System.out.println("🔥 [" + workerId + "] Xử lý Task [" + taskId + "] Type: " + typeEnum);
            System.out.println("   📋 Public ID from DB: " + publicId);
            System.out.println("   📄 Saved Name: " + savedName);
            System.out.println("   📄 Original Name: " + originalName);
            System.out.println("   📄 Input Extension: " + inputExtension);

            // 3. Mark Processing
            taskDAO.markTaskProcessing(taskId, workerId);

            // 3. XÁC ĐỊNH URL ĐỂ DOWNLOAD
            String filePath = file.getFile_path();
            String downloadUrl = null;

            if (filePath != null && !filePath.trim().isEmpty() && filePath.startsWith("http")) {
                downloadUrl = filePath;
                System.out.println("   ✅ Using file_path from database");
            } else if (publicId != null && !publicId.trim().isEmpty()) {
                downloadUrl = CloudUploadService.generateSignedUrl(publicId);
                System.out.println("   🔗 Generated URL from public_id");
            } else {
                throw new Exception("Both file_path and public_id are invalid!");
            }

            // 4. DOWNLOAD FILE
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 20, "Downloading file...");
            System.out.println("   ⬇️ Downloading from URL...");
            tempInput = java.nio.file.Files.createTempFile("input_", inputExtension);
            
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
                    return false; // Exit processNextJob() để xử lý task tiếp theo
                } else {
                    throw e;
                }
            }

            // 5. Xác định đuôi file đầu ra & Tạo Temp Output
            Thread.sleep(200);
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 30, "Preparing output...");
            String outputExt = getOutputExtension(typeEnum);
            tempOutput = java.nio.file.Files.createTempFile("output_", outputExt);

            // 6. CONVERT
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 40, "Converting file...");
            System.out.println("   ⚙️ Converting (" + typeEnum + ")...");

            switch (typeEnum) {
                case PDF_TO_DOCX:
                    PdfTool.convertPdfToDocx(tempInput.toString(), tempOutput.toString());
                    break;
                case DOCX_TO_PDF:
                    new docx_to_pdf_service().convertDocxtoPdf(tempInput.toString(), tempOutput.toString());
                    break;
                
                case PPTX_TO_PDF:
                    pptx_to_pdf_service pptxService = new pptx_to_pdf_service();
                    pptxService.convertPptxToPdf(tempInput.toString(), tempOutput.toString());
                    break;
                
                case CSV_TO_JSON:
                    csv_to_json_service csvService = new csv_to_json_service();
                    csvService.convertCsvToJson(tempInput.toString(), tempOutput.toString());
                    break;
                
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
                
                case HTML_TO_MARKDOWN:
                    html_to_markdown_service htmlToMdService = new html_to_markdown_service();
                    htmlToMdService.convertHtmlToMd(tempInput.toString(), tempOutput.toString());
                    break;
                
                case MARKDOWN_TO_HTML:
                    markdown_to_html_service mdToHtmlService = new markdown_to_html_service();
                    mdToHtmlService.convertMdToHtml(tempInput.toString(), tempOutput.toString());
                    break;
                
                case IMAGE_TO_PDF:
                    image_to_pdf_service imgToPdfService = new image_to_pdf_service();
                    imgToPdfService.convertImageToPdf(tempInput.toString(), tempOutput.toString());
                    break;
                
                case PDF_TO_IMAGE:
                    pdf_to_image_service pdfToImgService = new pdf_to_image_service();
                    // PDF to image cần folder để lưu nhiều ảnh
                    Path outputFolder = Path.of(tempOutput.toString().replace(".zip", "_images"));
                    java.nio.file.Files.createDirectories(outputFolder);
                    pdfToImgService.convertPdfToImage(tempInput.toString(), outputFolder.toString());
                    // Zip tất cả images thành 1 file zip (deleteFolder đã được gọi trong zipFolder)
                    zipFolder(outputFolder, tempOutput);
                    break;
                
                case XLSX_TO_CSV:
                    xlsx_to_csv_service xlsxToCsvService = new xlsx_to_csv_service();
                    xlsxToCsvService.convertXlsxToCsv(tempInput.toString(), tempOutput.toString());
                    break;
                
                case IMG_FORMAT:
                    image_format_service imgFormatService = new image_format_service();
                    // Default: convert to PNG (universal format)
                    imgFormatService.convertImage(tempInput.toString(), tempOutput.toString(), "png");
                    break;

                default:
                    throw new UnsupportedOperationException("Chưa hỗ trợ kiểu convert: " + typeEnum);
            }

            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 70, "Uploading result...");

            // 7. Upload Output
            Map<String, Object> uploadResult = CloudUploadService.uploadFile(
                    tempOutput.toFile(),
                    "result_" + savedName,
                    typeEnum.name());

            String newPublicId = (String) uploadResult.get("public_id");
            String format = (String) uploadResult.get("format");

            if (format == null || "null".equals(format)) {
                String ext = getOutputExtension(typeEnum);
                format = ext.startsWith(".") ? ext.substring(1) : ext;
            }

            String newSavedName = (savedName.contains(".") ? savedName.substring(0, savedName.lastIndexOf('.'))
                    : savedName) + "." + format;
            long newSize = ((Number) uploadResult.get("bytes")).longValue();

            // 8. Cập nhật DB
            fileDAO.updateConvertedFile(fileId, newPublicId, newSavedName, newSize, newPublicId);
            taskDAO.updateStatus(taskId, TaskStatus.COMPLETED, 100, "Done");

            System.out.println("✅ Task " + taskId + " HOÀN THÀNH! Public ID: " + newPublicId);
            return true; // Task completed successfully

        } catch (Exception ex) {
            ex.printStackTrace();
            if (taskId > 0) {
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "Error: " + ex.getMessage());
            }
            return false; // Task failed
        } finally {
            // 9. Cleanup (Quan trọng)
            try {
                if (tempInput != null) java.nio.file.Files.deleteIfExists(tempInput);
                if (tempOutput != null) java.nio.file.Files.deleteIfExists(tempOutput);
            } catch (Exception e) {
                System.err.println("Failed to cleanup temp files: " + e.getMessage());
            }
        }
    }
    
    /**
     * Phase 3: Process specific task by ID (from RabbitMQ)
     * 
     * RabbitMQ consumer receives task ID → Calls this method → Reuses processNextJob logic
     * 
     * Strategy: Lock the task first, then processNextJob will pick it up
     */
    /**
     * Process a specific task by ID (for RabbitMQ consumers)
     * Phase 3: RabbitMQ delivers task ID, process it directly
     */
    public void processJobById(long taskId) {
        System.out.println("🎯 [RabbitMQ→Worker " + workerId + "] Received task: " + taskId);
        Path tempInput = null;
        Path tempOutput = null;
        
        try {
            // 1. Get task from database
            Tasks task = taskDAO.getTaskById(taskId);
            
            if (task == null) {
                System.err.println("❌ Task " + taskId + " not found in database");
                return;
            }
            
            // ⚠️ PHASE 3: Don't check status here - WorkerPoolManager already marked as PROCESSING
            // Just proceed with conversion
            
            String fileId = task.getFileId();
            TaskType typeEnum = task.getTask_type();
            
            // 3. Get file info
            Files file = fileDAO.getFileByID(fileId);
            if (file == null) {
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "File not found in DB");
                return;
            }

            String publicId = file.getPublic_id();
            String savedName = file.getSaved_name();
            String originalName = file.getOriginal_name();
            
            String inputExtension = "";
            if (originalName != null && originalName.contains(".")) {
                inputExtension = originalName.substring(originalName.lastIndexOf("."));
            }

            System.out.println("🔥 [" + workerId + "] Processing Task [" + taskId + "] Type: " + typeEnum);
            System.out.println("   📋 Public ID: " + publicId);
            System.out.println("   📄 File: " + originalName);

            // 4. Get download URL
            String filePath = file.getFile_path();
            String downloadUrl = null;
            
            if (filePath != null && !filePath.trim().isEmpty() && filePath.startsWith("http")) {
                downloadUrl = filePath;
            } else if (publicId != null && !publicId.trim().isEmpty()) {
                downloadUrl = CloudUploadService.generateSignedUrl(publicId);
            } else {
                throw new Exception("No valid file URL available");
            }
            
            // 5. Download file
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 20, "Downloading...");
            System.out.println("   ⬇️ Downloading...");
            tempInput = java.nio.file.Files.createTempFile("input_", inputExtension);
            
            try (InputStream in = new java.net.URI(downloadUrl).toURL().openStream()) {
                java.nio.file.Files.copy(in, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 30, "Download complete");
            
            // 6. Convert file
            System.out.println("   🔄 Converting...");
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 40, "Converting...");
            
            String outputExtension = getOutputExtension(typeEnum);
            tempOutput = java.nio.file.Files.createTempFile("output_", outputExtension);
            
            // Perform conversion based on task type
            switch (typeEnum) {
                case PDF_TO_DOCX:
                    PdfTool.convertPdfToDocx(tempInput.toString(), tempOutput.toString());
                    break;
                case DOCX_TO_PDF:
                    new docx_to_pdf_service().convertDocxtoPdf(tempInput.toString(), tempOutput.toString());
                    break;
                case PPTX_TO_PDF:
                    new pptx_to_pdf_service().convertPptxToPdf(tempInput.toString(), tempOutput.toString());
                    break;
                case CSV_TO_JSON:
                    new csv_to_json_service().convertCsvToJson(tempInput.toString(), tempOutput.toString());
                    break;
                case DOCX_TO_XML:
                    new docx_to_xml_service().convertDocxToXml(tempInput.toString(), tempOutput.toString());
                    break;
                case XML_TO_DOCX:
                    new xml_to_docx_service().convertXmlToDocx(tempInput.toString(), tempOutput.toString());
                    break;
                case DOCX_TO_HTML:
                    new docx_to_html_service().convertDocxtoHtml(tempInput.toString(), tempOutput.toString());
                    break;
                case DOCX_TO_TXT:
                    new docx_to_txt_service().convertDocxToTxt(tempInput.toString(), tempOutput.toString());
                    break;
                case DOCX_TO_MARKDOWN:
                    new docx_to_markdown_service().convertDocxToMarkdown(tempInput.toString(), tempOutput.toString());
                    break;
                case HTML_TO_MARKDOWN:
                    new html_to_markdown_service().convertHtmlToMd(tempInput.toString(), tempOutput.toString());
                    break;
                case MARKDOWN_TO_HTML:
                    new markdown_to_html_service().convertMdToHtml(tempInput.toString(), tempOutput.toString());
                    break;
                case IMAGE_TO_PDF:
                    new image_to_pdf_service().convertImageToPdf(tempInput.toString(), tempOutput.toString());
                    break;
                case IMG_FORMAT:
                    new image_format_service().convertImage(tempInput.toString(), tempOutput.toString(), outputExtension);
                    break;
                case PDF_TO_IMAGE:
                    new pdf_to_image_service().convertPdfToImage(tempInput.toString(), tempOutput.toString());
                    break;
                case XLSX_TO_CSV:
                    new xlsx_to_csv_service().convertXlsxToCsv(tempInput.toString(), tempOutput.toString());
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported conversion type: " + typeEnum);
            }
            
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 70, "Conversion complete");
            
            // 7. Upload result
            System.out.println("   ☁️ Uploading result...");
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 80, "Uploading...");
            
            Map<String, Object> uploadResult = CloudUploadService.uploadFile(
                tempOutput.toFile(),
                "result_" + savedName,
                typeEnum.name()
            );
            
            String newPublicId = (String) uploadResult.get("public_id");
            String newUrl = (String) uploadResult.get("secure_url");
            String format = (String) uploadResult.get("format");
            
            if (format == null || format.isEmpty()) {
                format = outputExtension.substring(1);
            }
            
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 90, "Upload complete");
            
            // 8. Update file record
            String originalBaseName = savedName.contains(".") ? savedName.substring(0, savedName.lastIndexOf('.')) : savedName;
            String newSavedName = originalBaseName + "." + format;
            long newSize = ((Number) uploadResult.get("bytes")).longValue();
            fileDAO.updateConvertedFile(fileId, newPublicId, newSavedName, newSize, newPublicId);
            
            // 9. Mark as completed
            taskDAO.updateStatus(taskId, TaskStatus.COMPLETED, 100, "Done");
            System.out.println("✅ [" + workerId + "] Task " + taskId + " COMPLETED!");
            
        } catch (Exception ex) {
            System.err.println("❌ [" + workerId + "] Task " + taskId + " FAILED: " + ex.getMessage());
            ex.printStackTrace();
            
            try {
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "Error: " + ex.getMessage());
            } catch (Exception e2) {
                System.err.println("Failed to update failure status: " + e2.getMessage());
            }
        } finally {
            // Cleanup temp files
            try {
                if (tempInput != null) java.nio.file.Files.deleteIfExists(tempInput);
                if (tempOutput != null) java.nio.file.Files.deleteIfExists(tempOutput);
            } catch (Exception e) {
                System.err.println("Failed to cleanup temp files: " + e.getMessage());
            }
        }
    }

    private String getOutputExtension(TaskType type) {
        switch (type) {
            case DOCX_TO_PDF:
            case PPTX_TO_PDF:
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
            
            case HTML_TO_MARKDOWN:
                return ".md";
            
            case MARKDOWN_TO_HTML:
                return ".html";
            
            case PDF_TO_IMAGE:
                return ".zip"; // Zip file chứa nhiều ảnh PNG
            
            case XLSX_TO_CSV:
                return ".csv";
            
            case IMG_FORMAT:
                return ".png"; // Default to PNG

            default:
                return ".bin";
        }
    }
    
    /**
     * Zip folder thành 1 file zip
     */
    private void zipFolder(Path sourceFolder, Path zipFile) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
            java.nio.file.Files.walk(sourceFolder)
                .filter(path -> !java.nio.file.Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        ZipEntry zipEntry = new ZipEntry(sourceFolder.relativize(path).toString());
                        zos.putNextEntry(zipEntry);
                        java.nio.file.Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        }
        
        // Xóa folder sau khi zip xong
        deleteFolder(sourceFolder);
    }
    
    /**
     * Xóa folder và tất cả nội dung bên trong
     */
    private void deleteFolder(Path folder) throws Exception {
        if (java.nio.file.Files.exists(folder)) {
            java.nio.file.Files.walk(folder)
                .sorted((a, b) -> -a.compareTo(b)) // Xóa file trước, folder sau
                .forEach(path -> {
                    try {
                        java.nio.file.Files.delete(path);
                    } catch (Exception e) {
                        System.err.println("Failed to delete: " + path);
                    }
                });
        }
    }
    
}
