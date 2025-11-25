package com.convertfile.worker;

import com.convertfile.config.RabbitMQConnect;
import com.convertfile.model.dao.TaskDAO;
import com.convertfile.model.dao.FileDAO;
import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.Files;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Worker Pool Manager - Phase 3 (RabbitMQ Consumer)
 * 
 * BEFORE (Phase 2): Poll DB mỗi 1 giây → 1s latency
 * AFTER (Phase 3): RabbitMQ push instant → 0ms latency
 * 
 * Architecture:
 * Phase 2: Web Server → DB → Worker Poll (1s delay)
 * Phase 3: Web Server → DB + RabbitMQ → Worker Consume (instant)
 * 
 * RabbitMQ Benefits:
 * - Zero polling overhead (no SELECT queries)
 * - Instant delivery (push-based)
 * - Fair distribution (basicQos = 1)
 * - Multiple servers can consume same queue
 * 
 * Throughput: 10 tasks/min (Phase 2) → 10 tasks/min (same, but 0 latency)
 */
public class WorkerPoolManager {
    
    private static final boolean USE_RABBITMQ = true; // Toggle Phase 2/3
    
    private final int poolSize;
    private final ExecutorService workerPool;
    private Thread dispatcherThread;
    private volatile boolean running = false;
    
    /**
     * @param poolSize Số lượng worker threads (recommend: 5)
     */
    public WorkerPoolManager(int poolSize) {
        this.poolSize = poolSize;
        this.workerPool = Executors.newFixedThreadPool(poolSize);
        System.out.println("🔧 Worker Pool created with " + poolSize + " threads");
    }
    
    /**
     * Start worker pool
     * Phase 2: Poll DB → Submit to pool
     * Phase 3: Consume RabbitMQ → Submit to pool
     */
    public void start() {
        if (running) {
            System.err.println("⚠️ Worker pool already running!");
            return;
        }
        
        running = true;
        
        if (USE_RABBITMQ) {
            startRabbitMQConsumer();
        } else {
            startDatabasePoller();
        }
        
        System.out.println("✅ Worker Pool started successfully");
    }
    
    /**
     * Phase 3: RabbitMQ Consumer Mode
     * 
     * Architecture:
     * 1. Create consumer channel with basicQos(1) for fair distribution
     * 2. Register callback to receive messages
     * 3. Process task in worker pool
     * 4. Acknowledge message after completion
     * 
     * Benefits:
     * - Instant delivery (no polling delay)
     * - Fair distribution (each worker gets 1 task at a time)
     * - Reliable (manual ack only after success)
     */
    private void startRabbitMQConsumer() {
        dispatcherThread = new Thread(() -> {
            System.out.println("🚀 RabbitMQ Consumer thread started");
            
            try {
                // Create consumer channel with fair distribution
                Channel channel = RabbitMQConnect.createConsumerChannel();
                
                // Create worker instances
                FileWorker[] workers = new FileWorker[poolSize];
                for (int i = 0; i < poolSize; i++) {
                    workers[i] = new FileWorker("RabbitMQ-Worker-" + (i + 1));
                }
                
                // Message delivery callback
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    long taskId = Long.parseLong(message);
                    long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                    
                    System.out.println("📥 [" + java.time.LocalTime.now() + "] Received task from RabbitMQ: " + taskId);
                    System.out.println("   🔢 Active threads in pool: " + ((java.util.concurrent.ThreadPoolExecutor) workerPool).getActiveCount() + "/" + poolSize);
                    System.out.println("   📋 Queue size: " + ((java.util.concurrent.ThreadPoolExecutor) workerPool).getQueue().size());
                    
                    // Submit to worker pool (async execution)
                    System.out.println("🚀 [" + java.time.LocalTime.now() + "] Submitting task " + taskId + " to worker pool...");
                    workerPool.submit(() -> {
                        try {
                            System.out.println("⚡ [" + java.time.LocalTime.now() + "] Thread " + Thread.currentThread().getName() + " starting task " + taskId);
                            
                            // ✅ FAIR SCHEDULING: Check if user already has too many tasks processing
                            TaskDAO taskDAO = new TaskDAO();
                            Tasks task = taskDAO.getTaskById(taskId);
                            
                            if (task != null) {
                                // Get user_id from files table
                                FileDAO fileDAO = new FileDAO();
                                Files file = fileDAO.getFileByID(task.getFileId());
                                
                                if (file != null) {
                                    long userId = file.getUser_id();
                                    
                                    // ⚠️ CRITICAL: Mark as PROCESSING FIRST to prevent race condition
                                    // If we check count before marking, multiple threads can pass the check simultaneously
                                    taskDAO.markTaskProcessing(taskId, Thread.currentThread().getName());
                                    
                                    // Now count AFTER marking this task as processing
                                    int userActiveTasks = taskDAO.countProcessingTasksByUser(userId);
                                    
                                    System.out.println("   👤 User " + userId + " has " + userActiveTasks + " tasks processing (including this one)");
                                    
                                    // If user now has more than 2 tasks processing, revert and requeue
                                    if (userActiveTasks > 2) {
                                        System.out.println("   ⚠️ User " + userId + " exceeded limit (" + userActiveTasks + "/2 concurrent tasks), reverting and requeueing task " + taskId);
                                        
                                        // Revert status back to WAITING
                                        taskDAO.updateStatus(taskId, com.convertfile.model.bean.EnumStatus.TaskStatus.WAITING, 0, "Requeued for fairness");
                                        
                                        // Requeue with delay
                                        channel.basicNack(deliveryTag, false, true);
                                        Thread.sleep(2000); // 2s delay to let other tasks finish
                                        return;
                                    }
                                }
                            }
                            
                            // Find available worker and process
                            FileWorker worker = workers[0]; // Simple round-robin
                            worker.processTask(taskId);
                            
                            System.out.println("✨ [" + java.time.LocalTime.now() + "] Thread " + Thread.currentThread().getName() + " finished task " + taskId);
                            
                            // Acknowledge after successful processing
                            channel.basicAck(deliveryTag, false);
                            System.out.println("✅ [" + java.time.LocalTime.now() + "] Task " + taskId + " acknowledged (ACK to RabbitMQ)");
                            
                        } catch (Exception e) {
                            System.err.println("❌ Failed to process task " + taskId + ": " + e.getMessage());
                            try {
                                // Negative acknowledge - requeue for retry
                                channel.basicNack(deliveryTag, false, true);
                            } catch (IOException ex) {
                                System.err.println("Failed to nack message: " + ex.getMessage());
                            }
                        }
                    });
                };
                
                // Start consuming
                channel.basicConsume(
                    RabbitMQConnect.TASK_QUEUE,
                    false, // Manual acknowledge
                    deliverCallback,
                    consumerTag -> {
                        System.out.println("⚠️ Consumer cancelled: " + consumerTag);
                    }
                );
                
                System.out.println("🎧 Listening for tasks on RabbitMQ queue: " + RabbitMQConnect.TASK_QUEUE);
                
                // Keep thread alive
                while (running && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                }
                
            } catch (Exception e) {
                System.err.println("❌ RabbitMQ consumer error: " + e.getMessage());
                System.err.println("⚠️ Falling back to database polling mode...");
                startDatabasePoller();
            }
            
            System.out.println("🛑 RabbitMQ Consumer thread stopped");
        }, "WorkerPool-RabbitMQ-Consumer");
        
        dispatcherThread.start();
    }
    
    /**
     * Phase 2: Database Polling Mode (Fallback)
     */
    private void startDatabasePoller() {
        dispatcherThread = new Thread(() -> {
            System.out.println("🚀 Database Polling thread started (Phase 2 mode)");
            
            FileWorker[] workers = new FileWorker[poolSize];
            for (int i = 0; i < poolSize; i++) {
                workers[i] = new FileWorker("DB-Worker-" + (i + 1));
            }
            
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    // Poll tasks và submit vào pool
                    for (FileWorker worker : workers) {
                        if (running) {
                            workerPool.submit(worker::processOneTask);
                        }
                    }
                    
                    // Sleep để tránh tight loop
                    Thread.sleep(1000);
                    
                } catch (InterruptedException e) {
                    System.out.println("⚠️ Database polling thread interrupted");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Error in polling thread:");
                    e.printStackTrace();
                }
            }
            
            System.out.println("🛑 Database Polling thread stopped");
        }, "WorkerPool-DB-Poller");
        
        dispatcherThread.start();
    }
    
    /**
     * Graceful shutdown worker pool
     * 1. Stop accepting new tasks
     * 2. Wait for running tasks to complete
     * 3. Force shutdown if timeout
     */
    public void shutdown() {
        if (!running) {
            return;
        }
        
        System.out.println("🛑 Shutting down worker pool...");
        running = false;
        
        // Stop dispatcher thread
        if (dispatcherThread != null) {
            dispatcherThread.interrupt();
            try {
                dispatcherThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Shutdown worker pool
        workerPool.shutdown();
        
        try {
            // Wait for running tasks to complete (max 30 seconds)
            if (!workerPool.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("⚠️ Worker pool timed out, forcing shutdown...");
                workerPool.shutdownNow();
                
                // Wait again for forced shutdown
                if (!workerPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("❌ Worker pool did not terminate!");
                }
            }
            
            System.out.println("✅ Worker pool shut down successfully");
            
        } catch (InterruptedException e) {
            System.err.println("❌ Interrupted while shutting down worker pool");
            workerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Get worker pool status
     */
    public String getStatus() {
        return String.format(
            "Worker Pool Status - Running: %s, Pool Size: %d, Active: ~%d, Shutdown: %s",
            running,
            poolSize,
            poolSize - ((java.util.concurrent.ThreadPoolExecutor) workerPool).getActiveCount(),
            workerPool.isShutdown()
        );
    }
}
