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
        // Khi tắt server thì ngắt luồng (đơn giản hóa, không cần gọi .stop())
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
        }
        System.out.println("🛑 APP LISTENER: Server đã tắt.");
    }
}