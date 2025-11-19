package com.convertfile.config;

import com.convertfile.worker.FileWorker;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// Cái dòng này cực quan trọng: Nó báo cho Tomcat biết đây là file cấu hình khởi động
@WebListener
public class AppListener implements ServletContextListener {

    private Thread workerThread;
    private FileWorker fileWorker;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Khi Tomcat vừa bật -> Tạo một luồng riêng cho Worker
        fileWorker = new FileWorker();
        workerThread = new Thread(fileWorker);
        workerThread.start(); // Chạy ngay đi!
        System.out.println("🚀 APP LISTENER: Đã bật Background Worker!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (fileWorker != null) {
            fileWorker.stop();
        }
        if (workerThread != null) {
            workerThread.interrupt();
            try {
                workerThread.join(5000); // Chờ tối đa 5 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("🛑 APP LISTENER: Server đã tắt và Worker đã dừng.");
    }
}