package com.convertfile.config;

import com.convertfile.worker.FileWorker;
import com.convertfile.worker.WorkerPoolManager;
import com.convertfile.scheduler.FileCleanupScheduler;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {

    private WorkerPoolManager workerPoolManager; // Phase 2: Worker pool thay vì single thread
    private Thread cleanupThread;
    private FileCleanupScheduler cleanupScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Use ServletContext log to ensure logs are written
        sce.getServletContext().log("========================================");
        sce.getServletContext().log("🚀 CONVERT_FILE AppListener STARTING...");
        sce.getServletContext().log("========================================");
        
        // Load MySQL driver trước khi start worker thread
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            sce.getServletContext().log("✅ MySQL Driver loaded successfully");
            System.out.println("✅ MySQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            sce.getServletContext().log("❌ MySQL Driver not found!");
            System.err.println("❌ MySQL Driver not found!");
            e.printStackTrace();
        }
        
        // Phase 2: Initialize HikariCP connection pool
        sce.getServletContext().log("🔧 Initializing HikariCP connection pool...");
        System.out.println("🔧 Initializing HikariCP connection pool...");
        try {
            DBConnect.getConnection().close(); // Test connection
            sce.getServletContext().log("✅ HikariCP connection pool ready");
            System.out.println("✅ HikariCP connection pool ready");
        } catch (Exception e) {
            sce.getServletContext().log("❌ Failed to initialize connection pool: " + e.getMessage());
            System.err.println("❌ Failed to initialize connection pool");
            e.printStackTrace();
        }
        
        // Phase 3: Initialize RabbitMQ connection
        sce.getServletContext().log("🐰 Initializing RabbitMQ...");
        System.out.println("🐰 Initializing RabbitMQ...");
        try {
            RabbitMQConnect.initialize();
            sce.getServletContext().log("✅ RabbitMQ connection ready (localhost:5672)");
            System.out.println("✅ RabbitMQ connection ready (localhost:5672)");
        } catch (Exception e) {
            sce.getServletContext().log("⚠️ RabbitMQ initialization failed: " + e.getMessage());
            System.err.println("⚠️ RabbitMQ initialization failed - falling back to Phase 2 mode");
            System.err.println("   Error: " + e.getMessage());
        }
        
        // Phase 3: Initialize Redis connection and Pub/Sub
        sce.getServletContext().log("📡 Initializing Redis...");
        System.out.println("📡 Initializing Redis...");
        try {
            RedisConnect.initialize();
            
            // Subscribe to task updates from other servers
            RedisConnect.subscribeTaskUpdates(message -> {
                try {
                    // Parse JSON message: {"taskId":123,"status":"COMPLETED","progress":100,"message":"Done"}
                    String[] parts = message.replace("{", "").replace("}", "").replace("\"", "").split(",");
                    long taskId = Long.parseLong(parts[0].split(":")[1]);
                    String status = parts[1].split(":")[1];
                    int progress = Integer.parseInt(parts[2].split(":")[1]);
                    String msg = parts[3].substring(parts[3].indexOf(":") + 1);
                    
                    System.out.println("📨 [Redis Pub/Sub] Received update for task " + taskId);
                    com.convertfile.controller.JobWebSocket.broadcastFromRedis(taskId, status, progress, msg);
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to parse Redis message: " + message);
                }
            });
            
            sce.getServletContext().log("✅ Redis connection ready (localhost:6379)");
            System.out.println("✅ Redis connection ready (localhost:6379)");
        } catch (Exception e) {
            sce.getServletContext().log("⚠️ Redis initialization failed: " + e.getMessage());
            System.err.println("⚠️ Redis initialization failed - WebSocket sync disabled");
            System.err.println("   Error: " + e.getMessage());
        }
        
        // Phase 2: Start worker pool (5 threads) thay vì single thread
        sce.getServletContext().log("🚀 Starting Worker Pool (5 threads)...");
        workerPoolManager = new WorkerPoolManager(5);
        workerPoolManager.start();
        sce.getServletContext().log("✅ Worker Pool started successfully!");
        System.out.println("🚀 APP LISTENER: Đã bật Worker Pool (5 threads)!");
        
        // Khởi tạo File Cleanup Scheduler
        sce.getServletContext().log("🧹 Starting File Cleanup Scheduler...");
        cleanupScheduler = new FileCleanupScheduler();
        cleanupThread = new Thread(cleanupScheduler);
        cleanupThread.setContextClassLoader(this.getClass().getClassLoader());
        cleanupThread.start();
        sce.getServletContext().log("✅ File Cleanup Scheduler started!");
        System.out.println("🧹 APP LISTENER: Đã bật File Cleanup Scheduler!");
        
        sce.getServletContext().log("========================================");
        sce.getServletContext().log("✅ CONVERT_FILE STARTUP COMPLETE");
        sce.getServletContext().log("========================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 APP LISTENER: Đang tắt server...");

        // Phase 2: Shutdown worker pool
        if (workerPoolManager != null) {
            workerPoolManager.shutdown();
        }
        
        // Phase 3: Close RabbitMQ connection
        try {
            RabbitMQConnect.close();
            System.out.println("✅ RabbitMQ connection closed");
        } catch (Exception e) {
            System.err.println("⚠️ Error closing RabbitMQ: " + e.getMessage());
        }
        
        // Phase 3: Close Redis connection
        try {
            RedisConnect.close();
            System.out.println("✅ Redis connection closed");
        } catch (Exception e) {
            System.err.println("⚠️ Error closing Redis: " + e.getMessage());
        }
        
        // Close HikariCP connection pool
        DBConnect.closePool();

        // Dừng cleanup scheduler
        if (cleanupScheduler != null) {
            cleanupScheduler.stop();
        }

        if (cleanupThread != null && cleanupThread.isAlive()) {
            cleanupThread.interrupt();

            try {
                cleanupThread.join(5000);

                if (cleanupThread.isAlive()) {
                    System.out.println("⚠️ Cleanup thread không dừng sau 5 giây!");
                } else {
                    System.out.println("✅ Cleanup thread đã dừng thành công.");
                }
            } catch (InterruptedException e) {
                System.out.println("⚠️ Bị gián đoạn khi đợi cleanup thread.");
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("🛑 APP LISTENER: Server đã tắt hoàn toàn.");
    }
}
