package com.convertfile.worker;

import com.convertfile.model.dao.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FileWorker implements Runnable {

    // Sử dụng while(true) nên không cần biến 'running'
    @Override
    public void run() {
        System.out.println("🤖 WORKER (VERSION REAL) ĐÃ KHỞI ĐỘNG...");
        while (true) {
            try {
                processNextJob();
                Thread.sleep(2000); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processNextJob() {
        Connection conn = null;
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return;

            String sqlFind = "SELECT * FROM tasks JOIN files ON tasks.file_id = files.file_id WHERE tasks.status = 'WAITING' LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sqlFind);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String taskId = rs.getString("task_id");
                String inputPath = rs.getString("input_path");
                String savedName = rs.getString("saved_name");

                System.out.println("🔥 Bắt đầu xử lý Task ID: " + taskId);
                
                // Đổi tên file output
                String outputPath = inputPath.substring(0, inputPath.lastIndexOf(".")) + ".docx";
                if(inputPath.endsWith(".pdf") == false && inputPath.endsWith(".PDF") == false) {
                     outputPath = inputPath + ".docx"; // Fallback nếu tên file lạ
                }

                try {
                    // 1. Đếm trang
                    int totalPages = com.convertfile.service.PdfTool.getPageCount(inputPath);
                    if (totalPages == 0) totalPages = 1;

                    updateStatus(conn, taskId, "PROCESSING", 0);

                    // 2. Chạy từng trang
                    for (int i = 0; i < totalPages; i++) {
                        // Chỉ convert nếu là PDF
                        if (savedName.toLowerCase().endsWith(".pdf")) {
                            com.convertfile.service.PdfTool.convertPdfToDocx(inputPath, outputPath);
                        } else {
                            Thread.sleep(500); // Giả lập nếu không phải PDF
                        }
                        
                        // Vì convert docx làm 1 lèo, nên ta giả lập % chạy cho đẹp
                        // (Logic thực tế ở đây ta làm đơn giản hóa để tránh phức tạp)
                    }
                    
                    // Giả lập chạy vèo vèo 10% -> 100% để user thấy
                    for(int k=10; k<=100; k+=10) {
                         updateStatus(conn, taskId, "PROCESSING", k);
                         Thread.sleep(100);
                    }
                    
                    updateStatus(conn, taskId, "COMPLETED", 100);
                    System.out.println("✅ Task " + taskId + " HOÀN THÀNH!");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    updateStatus(conn, taskId, "FAILED", 0);
                    System.out.println("❌ Lỗi xử lý: " + ex.getMessage());
                }
            }
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    private void updateStatus(Connection conn, String taskId, String status, int progress) {
        try {
            String sql = "UPDATE tasks SET status = ?, progress_percent = ? WHERE task_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, progress);
            ps.setString(3, taskId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}