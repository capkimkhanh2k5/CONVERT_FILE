package com.convertfile.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.convertfile.config.DBConnect;
import com.convertfile.controller.JobWebSocket;
import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.bean.EnumStatus.TaskType;

public class TaskDAO {
    /**
     * Insert task and retrieve auto-generated task_id (Phase 3)
     * 
     * BEFORE (Phase 2): insertTask() returns boolean
     * AFTER (Phase 3): insertTask() returns boolean + sets task_id in entity
     * 
     * This ID is needed to publish to RabbitMQ queue
     */
    public boolean insertTask(Tasks job) {
        String sql = """
                INSERT INTO tasks (file_id, task_type, status, message, worker_id, attempt_count, created_at, started_at, completed_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, job.getFileId());
            ps.setString(2, job.getTask_type().name());
            ps.setString(3, job.getStatus().name());
            ps.setString(4, job.getMessage());
            ps.setString(5, job.getWorker_id());
            ps.setInt(6, job.getAttempt_count());
            ps.setTimestamp(7, Timestamp.valueOf(job.getCreated_at()));
            ps.setTimestamp(8, job.getStarted_at() == null ? null : Timestamp.valueOf(job.getStarted_at()));
            ps.setTimestamp(9, job.getCompleted_at() == null ? null : Timestamp.valueOf(job.getCompleted_at()));

            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                // Retrieve auto-generated task_id
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        long generatedId = rs.getLong(1);
                        job.setTask_id(generatedId); // Set ID in entity for RabbitMQ publish
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy task tiếp theo từ queue với row-level lock (Phase 2)
     * 
     * SELECT FOR UPDATE SKIP LOCKED:
     * - FOR UPDATE: Lock row khi đọc → Worker khác không lấy được task này
     * - SKIP LOCKED: Bỏ qua row đang bị lock → Worker khác lấy task tiếp theo
     * 
     * Prevent race condition:
     * Worker 1: SELECT task_id=100 FOR UPDATE → Lock row 100
     * Worker 2: SELECT ... SKIP LOCKED → Lấy task_id=101 (skip 100)
     * 
     * @return Task WAITING tiếp theo, hoặc null nếu không có
     */
    public Tasks getNextWaitingTask() {
        String sql = """
                SELECT * FROM tasks
                WHERE status = ? 
                ORDER BY created_at ASC 
                LIMIT 1
                FOR UPDATE SKIP LOCKED
                """;

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TaskStatus.WAITING.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tasks job = new Tasks();
                    job.setTask_id(rs.getLong("task_id"));
                    job.setFileId(rs.getString("file_id"));
                    job.setTask_type(TaskType.valueOf(rs.getString("task_type")));
                    job.setStatus(TaskStatus.valueOf(rs.getString("status")));
                    job.setMessage(rs.getString("message"));
                    job.setWorker_id(rs.getString("worker_id"));
                    job.setAttempt_count(rs.getInt("attempt_count"));
                    job.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());

                    Timestamp started = rs.getTimestamp("started_at");
                    Timestamp completed = rs.getTimestamp("completed_at");
                    if (started != null)
                        job.setStarted_at(started.toLocalDateTime());
                    if (completed != null)
                        job.setCompleted_at(completed.toLocalDateTime());

                    return job;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Get task by specific ID (Phase 3 - for RabbitMQ consumer)
     */
    public Tasks getTaskById(long taskId) {
        String sql = """
                SELECT * FROM tasks
                WHERE task_id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, taskId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tasks job = new Tasks();
                    job.setTask_id(rs.getLong("task_id"));
                    job.setFileId(rs.getString("file_id"));
                    job.setTask_type(TaskType.valueOf(rs.getString("task_type")));
                    job.setStatus(TaskStatus.valueOf(rs.getString("status")));
                    job.setMessage(rs.getString("message"));
                    job.setWorker_id(rs.getString("worker_id"));
                    job.setAttempt_count(rs.getInt("attempt_count"));
                    job.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());

                    Timestamp started = rs.getTimestamp("started_at");
                    Timestamp completed = rs.getTimestamp("completed_at");
                    if (started != null)
                        job.setStarted_at(started.toLocalDateTime());
                    if (completed != null)
                        job.setCompleted_at(completed.toLocalDateTime());

                    return job;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateStatus(long taskID, TaskStatus status, int progressPercent, String message) {
        String sql = """
                    UPDATE tasks
                    SET status = ?, progress_percent = ?, message = ?, completed_at = ?
                    WHERE task_id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, progressPercent);
            ps.setString(3, message);
            ps.setTimestamp(4,
                    (status == TaskStatus.COMPLETED || status == TaskStatus.FAILED)
                            ? Timestamp.valueOf(java.time.LocalDateTime.now())
                            : null);
            ps.setLong(5, taskID);

            ps.executeUpdate();
            
            // Phase 2: Broadcast to local WebSocket clients
            try {
                JobWebSocket.broadcastTaskUpdate(taskID, status.name(), progressPercent, message);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to broadcast WebSocket update: " + e.getMessage());
            }
            
            // Phase 3: Publish to Redis Pub/Sub for cross-server sync
            try {
                com.convertfile.config.RedisConnect.publishTaskUpdate(taskID, status.name(), progressPercent, message);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to publish to Redis: " + e.getMessage());
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markTaskProcessing(long taskID, String workerId) {
        String sql = """
                    UPDATE tasks
                    SET status = ?, worker_id = ?, started_at = ?
                    WHERE task_id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TaskStatus.PROCESSING.name());
            ps.setString(2, workerId);
            ps.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setLong(4, taskID);

            int rowsUpdated = ps.executeUpdate();
            System.out.println("   🔧 markTaskProcessing: Updated " + rowsUpdated + " rows for task " + taskID);
        } catch (SQLException e) {
            System.err.println("   ❌ markTaskProcessing FAILED for task " + taskID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Count PROCESSING tasks for a specific user (Fair Scheduling - Phase 3)
     * Used to limit concurrent tasks per user for fairness
     * 
     * @param userId User ID (0 for guest)
     * @return Number of tasks currently in PROCESSING status for this user
     */
    public int countProcessingTasksByUser(long userId) {
        // ⚠️ CRITICAL: Guest users (userId=0) have user_id=NULL in database
        // Must handle NULL case properly: (user_id IS NULL OR user_id = 0)
        String sql = """
                SELECT COUNT(*) as count
                FROM tasks t
                INNER JOIN files f ON t.file_id = f.file_id
                WHERE (f.user_id = ? OR (? = 0 AND f.user_id IS NULL))
                  AND t.status = 'PROCESSING'
                """;
        
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, userId);
            ps.setLong(2, userId); // Set twice for the OR condition
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("   🔍 countProcessingTasksByUser: Found " + count + " PROCESSING tasks for user " + userId);
                    return count;
                }
            }
        } catch (SQLException e) {
            System.err.println("   ❌ countProcessingTasksByUser FAILED for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}
