package com.convertfile.config;

import com.convertfile.worker.FileWorker;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// Cái dòng này cực quan trọng: Nó báo cho Tomcat biết đây là file cấu hình khởi động
@WebListener
public class AppListener implements ServletContextListener {

    private Thread workerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Khi Tomcat vừa bật -> Tạo một luồng riêng cho Worker
        workerThread = new Thread(new FileWorker());
        workerThread.start(); // Chạy ngay đi!
        System.out.println("🚀 APP LISTENER: Đã bật Background Worker!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Khi tắt Tomcat -> Có thể xử lý dừng thread ở đây (tạm bỏ qua)
        System.out.println("🛑 APP LISTENER: Server đã tắt.");
    }
}