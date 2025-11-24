package com.convertfile.worker;

import com.convertfile.model.dao.FileDAO;
import com.convertfile.model.dao.TaskDAO;
import com.convertfile.model.bean.Files;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.bean.EnumStatus.TaskType;
import com.convertfile.service.ConvertService.PdfTool;
import com.convertfile.service.CloudService.CloudUploadService;
import com.convertfile.service.ConvertService.docx_to_pdf_service;
import com.convertfile.service.ConvertService.csv_to_json_service;
import com.convertfile.service.RabbitMQService.RabbitMQConnection;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class FileWorker implements Runnable {

    private final TaskDAO taskDAO = new TaskDAO();
    private final FileDAO fileDAO = new FileDAO();

    @Override
    public void run() {
        System.out.println("🤖 WORKER (RABBITMQ) ĐÃ KHỞI ĐỘNG...");

        try {
            Connection connection = RabbitMQConnection.getConnection();
            Channel channel = connection.createChannel();

            // Đảm bảo queue tồn tại
            channel.queueDeclare(RabbitMQConnection.QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            // Giới hạn số lượng message chưa ack (QoS = 1) để tránh quá tải worker
            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                long taskId = 0;
                try {
                    JSONObject json = new JSONObject(message);
                    taskId = json.getLong("id");
                    String fileId = json.getString("fileId");
                    String typeStr = json.getString("type");
                    TaskType typeEnum = TaskType.valueOf(typeStr);

                    processTask(taskId, fileId, typeEnum);

                    // Xác nhận đã xử lý xong (ACK)
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception e) {
                    System.err.println("❌ Error processing message: " + e.getMessage());
                    e.printStackTrace();

                    if (taskId > 0) {
                        taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "Worker Error: " + e.getMessage());
                    }

                    // Nếu lỗi nghiêm trọng, có thể NACK để requeue hoặc reject
                    // Ở đây reject (false) và không requeue (false) để tránh lặp vô tận nếu lỗi
                    // code
                    channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            // AutoAck = false (Cần xác nhận thủ công sau khi xử lý xong)
            channel.basicConsume(RabbitMQConnection.QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });

            // Giữ thread sống để lắng nghe
            synchronized (this) {
                while (!Thread.currentThread().isInterrupted()) {
                    wait();
                }
            }

        } catch (Exception e) {
            System.err.println("❌ WORKER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processTask(long taskId, String fileId, TaskType typeEnum) throws Exception {
        Path tempInput = null;
        Path tempOutput = null;

        try {
            // 1. Lấy thông tin file
            Files file = fileDAO.getFileByID(fileId);
            if (file == null) {
                taskDAO.updateStatus(taskId, TaskStatus.FAILED, 0, "File not found in DB");
                return;
            }

            String publicId = file.getPublic_id();
            String savedName = file.getSaved_name();

            System.out.println("🔥 Xử lý Task [" + taskId + "] Type: " + typeEnum);
            System.out.println("   📋 Public ID from DB: " + publicId);
            System.out.println("   📄 Saved Name: " + savedName);

            // 2. Mark Processing
            taskDAO.markTaskProcessing(taskId, "Worker-RabbitMQ");

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
            tempInput = java.nio.file.Files.createTempFile("input_", "_" + savedName);

            try (InputStream in = new java.net.URI(downloadUrl).toURL().openStream()) {
                java.nio.file.Files.copy(in, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }
            taskDAO.updateStatus(taskId, TaskStatus.PROCESSING, 25, "Download completed");

            // 5. Chuẩn bị Output
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
                case CSV_TO_JSON:
                    new csv_to_json_service().convertCsvToJson(tempInput.toString(), tempOutput.toString());
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

            System.out.println("✅ Task " + taskId + " HOÀN THÀNH!");

        } finally {
            // 9. Cleanup
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
                return ".bin";
        }
    }
}
