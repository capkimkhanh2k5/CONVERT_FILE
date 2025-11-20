package com.convertfile.config;

import com.convertfile.worker.FileWorker;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {

    private Thread workerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Khởi tạo luồng chạy ngầm
        workerThread = new Thread(new FileWorker());
        workerThread.start();
        System.out.println("🚀 APP LISTENER: Đã bật Background Worker!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 APP LISTENER: Đang tắt server...");

        // Gửi tín hiệu interrupt đến worker thread
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();

            try {
                // Đợi worker thread dừng hoàn toàn (timeout 5 giây)
                workerThread.join(5000);

                if (workerThread.isAlive()) {
                    System.out.println("⚠️ Worker thread không dừng sau 5 giây!");
                } else {
                    System.out.println("✅ Worker thread đã dừng thành công.");
                }
            } catch (InterruptedException e) {
                System.out.println("⚠️ Bị gián đoạn khi đợi worker thread.");
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("🛑 APP LISTENER: Server đã tắt hoàn toàn.");
    }
}