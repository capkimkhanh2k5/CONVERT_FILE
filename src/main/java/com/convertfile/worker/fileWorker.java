package com.convertfile.worker;

import com.convertfile.model.dao.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FileWorker implements Runnable {

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
                
                // Đổi tên file output: .pdf -> .docx
                String outputPath = inputPath.substring(0, inputPath.lastIndexOf(".")) + ".docx";

                try {
                    // 1. Đếm trang để biết file nặng nhẹ
                    int totalPages = com.convertfile.service.PdfTool.getPageCount(inputPath);
                    System.out.println("   => Tổng số trang: " + totalPages);
                    
                    updateStatus(conn, taskId, "PROCESSING", 10);

                    // 2. Gọi hàm Convert sang DOCX (Hàm này chạy mất vài giây)
                    // Vì convert Docx không chia trang dễ như Text, ta làm 1 lèo luôn
                    if (savedName.toLowerCase().endsWith(".pdf")) {
                        com.convertfile.service.PdfTool.convertPdfToDocx(inputPath, outputPath);
                    }
                    
                    // Giả lập tiến độ nhảy vọt sau khi convert xong
                    for(int k=20; k<=90; k+=20) {
                        updateStatus(conn, taskId, "PROCESSING", k);
                        Thread.sleep(200); // Hiệu ứng thôi
                    }

                    // 3. Xong hết
                    updateStatus(conn, taskId, "COMPLETED", 100);
                    System.out.println("✅ Task " + taskId + " HOÀN THÀNH! File: " + outputPath);

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