package com.convertfile.service;

import com.convertfile.config.RabbitMQConnect;
import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.bean.EnumStatus.TaskType;
import com.convertfile.model.dao.TaskDAO;

/**
 * Task Queue Service (Phase 3)
 * 
 * BEFORE (Phase 2): Write to database → Worker polls
 * AFTER (Phase 3): Write to database + Publish to RabbitMQ → Worker consumes instantly
 */
public class TaskQueueService {
    private final TaskDAO taskDAO = new TaskDAO();
    private static final boolean USE_RABBITMQ = true; // Toggle for Phase 2/3

    public void addNewTask(String fileID, String jobType) {
        // 1. Create task entity
        Tasks job = new Tasks();
        job.setFileId(fileID);
        job.setTask_type(TaskType.valueOf(jobType));
        job.setStatus(TaskStatus.WAITING);
        job.setMessage("");
        job.setWorker_id("");
        job.setAttempt_count(0);

        // 2. Insert to database (for persistence & tracking)
        boolean inserted = taskDAO.insertTask(job);
        
        if (!inserted) {
            System.err.println("❌ Failed to insert task to database");
            return;
        }
        
        // 3. Get the task ID from database
        long taskId = job.getTask_id();
        
        // 4. Phase 3: Publish to RabbitMQ for instant delivery
        if (USE_RABBITMQ) {
            try {
                RabbitMQConnect.publishTask(taskId);
                System.out.println("✅ Task " + taskId + " published to RabbitMQ");
            } catch (Exception e) {
                System.err.println("⚠️ Failed to publish to RabbitMQ: " + e.getMessage());
                System.err.println("   Falling back to database polling (Phase 2 mode)");
                // Task still in DB, Phase 2 worker will pick it up
            }
        } else {
            System.out.println("ℹ️ RabbitMQ disabled, using database polling (Phase 2 mode)");
        }
    }
    
    /**
     * Get task ID after insertion (for RabbitMQ publish)
     */
    public long addNewTaskAndReturnId(String fileID, String jobType) {
        Tasks job = new Tasks();
        job.setFileId(fileID);
        job.setTask_type(TaskType.valueOf(jobType));
        job.setStatus(TaskStatus.WAITING);
        job.setMessage("");
        job.setWorker_id("");
        job.setAttempt_count(0);

        boolean inserted = taskDAO.insertTask(job);
        
        if (!inserted) {
            return -1;
        }
        
        long taskId = job.getTask_id();
        
        // Publish to RabbitMQ
        if (USE_RABBITMQ) {
            try {
                RabbitMQConnect.publishTask(taskId);
            } catch (Exception e) {
                System.err.println("⚠️ RabbitMQ publish failed: " + e.getMessage());
            }
        }
        
        return taskId;
    }
}
