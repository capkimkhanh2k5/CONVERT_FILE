package com.convertfile.worker;

import com.convertfile.model.dao.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FileWorker implements Runnable {

    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    // QUAN TRỌNG: Không được có bất kỳ hàm public FileWorker(...) nào ở đây cả!
    // Để trống thế này Java sẽ tự tạo constructor rỗng.

    @Override
    public void run() {
        System.out.println("🤖 WORKER ĐÃ KHỞI ĐỘNG - Đang chờ việc...");
        while (running) {
            try {
                processNextJob();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("🛑 WORKER ĐÃ DỪNG.");
    }

    private void processNextJob() {
        Connection conn = null;
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return;

            String sqlFind = "SELECT * FROM tasks WHERE status = 'WAITING' LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sqlFind);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String taskId = rs.getString("task_id");
                System.out.println("🔥 Đang xử lý Task ID: " + taskId);
                updateStatus(conn, taskId, "PROCESSING", 0);

                for (int i = 10; i <= 100; i += 10) {
                    Thread.sleep(1000);
                    updateStatus(conn, taskId, "PROCESSING", i);
                    System.out.println("   >>> Task " + taskId + ": " + i + "%");
                }

                updateStatus(conn, taskId, "COMPLETED", 100);
                System.out.println("✅ Task " + taskId + " HOÀN THÀNH!");
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