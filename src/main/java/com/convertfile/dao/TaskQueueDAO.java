package com.convertfile.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.convertfile.model.EnumStatus.TaskStatus;
import com.convertfile.model.EnumStatus.TaskType;
import com.convertfile.model.TaskJob;

public class TaskQueueDAO {
    public boolean insertTask(TaskJob job) {
    String sql = """
        INSERT INTO task_queue (file_id, task_type, status, message, worker_id, attempt_count, created_at, started_at, completed_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (Connection conn = ConnectDB.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, job.getFileId());
        ps.setString(2, job.getTask_type().name());
        ps.setString(3, job.getStatus().name());
        ps.setString(4, job.getMessage());
        ps.setString(5, job.getWorker_id());
        ps.setInt(6, job.getAttempt_count());
        ps.setTimestamp(7, Timestamp.valueOf(job.getCreated_at()));
        ps.setTimestamp(8, job.getStarted_at() == null ? null : Timestamp.valueOf(job.getStarted_at()));
        ps.setTimestamp(9, job.getCompleted_at() == null ? null : Timestamp.valueOf(job.getCompleted_at()));

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    public TaskJob getNextWaitingTask() {
        String sql = """
            SELECT * FROM tasks 
            WHERE status = ? ORDER BY created_at ASC LIMIT 1 
            """;

        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TaskStatus.WAITING.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaskJob job = new TaskJob();
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
                    if (started != null) job.setStarted_at(started.toLocalDateTime());
                    if (completed != null) job.setCompleted_at(completed.toLocalDateTime());

                    return job;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateStatus(long taskID, TaskStatus status, String message) {
        String sql = """
                        UPDATE tasks 
                        SET status = ?, message = ?, completed_at = ? 
                        WHERE task_id = ?
                    """;

        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, message);
            ps.setTimestamp(3, 
                (status == TaskStatus.COMPLETED || status == TaskStatus.FAILED)
                ? Timestamp.valueOf(java.time.LocalDateTime.now()) : null
            );
            ps.setLong(4, taskID);

            ps.executeUpdate();
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

        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TaskStatus.PROCESSING.name());
            ps.setString(2, workerId);
            ps.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setLong(4, taskID);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
