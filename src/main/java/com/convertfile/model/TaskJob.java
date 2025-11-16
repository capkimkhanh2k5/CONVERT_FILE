package com.convertfile.model;

import java.time.LocalDateTime;

import com.convertfile.model.EnumStatus.TaskStatus;
import com.convertfile.model.EnumStatus.TaskType;


public class TaskJob {
    private long task_id;
    private String file_id;
    private TaskType task_type;
    private TaskStatus status;
    private String message;
    private String worker_id;
    private int attempt_count;
    private LocalDateTime created_at;
    private LocalDateTime started_at;
    private LocalDateTime completed_at;



    // Constructor
    public TaskJob() {
        this.task_id = 0;
        this.file_id = "";
        this.task_type = null;
        this.status = TaskStatus.WAITING;
        this.message = "";
        this.worker_id = "";
        this.attempt_count = 0;
        this.created_at = LocalDateTime.now();
        this.started_at = null;
        this.completed_at = null;
    }

    public TaskJob(long task_id, String fileId, TaskType task_type, TaskStatus status, String message, String worker_id, int attempt_count, LocalDateTime created_at, LocalDateTime started_at, LocalDateTime completed_at) {
        this.task_id = task_id;
        this.file_id = fileId;
        this.task_type = task_type;
        this.status = status;
        this.message = message;
        this.worker_id = worker_id;
        this.attempt_count = attempt_count;
        this.created_at = created_at;
        this.started_at = started_at;
        this.completed_at = completed_at;
    }

    public TaskJob(TaskJob job){
        this.task_id = job.getTask_id();
        this.file_id = job.getFileId();
        this.task_type = job.getTask_type();
        this.status = job.getStatus();
        this.message = job.getMessage();
        this.worker_id = job.getWorker_id();
        this.attempt_count = job.getAttempt_count();
        this.created_at = job.getCreated_at();
        this.started_at = job.getStarted_at();
        this.completed_at = job.getCompleted_at();
    }

    // Getters & Setters
    public long getTask_id() { return task_id; }
    public void setTask_id(long task_id) { this.task_id = task_id; }

    public String getFileId() { return file_id; }
    public void setFileId(String fileId) { this.file_id = fileId; }

    public TaskType getTask_type() { return task_type; }
    public void setTask_type(TaskType task_type) { this.task_type = task_type; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getWorker_id() { return worker_id; }
    public void setWorker_id(String worker_id) { this.worker_id = worker_id; }

    public int getAttempt_count() { return attempt_count; }
    public void setAttempt_count(int attempt_count) { this.attempt_count = attempt_count; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public LocalDateTime getStarted_at() { return started_at; }
    public void setStarted_at(LocalDateTime started_at) { this.started_at = started_at; }

    public LocalDateTime getCompleted_at() { return completed_at; }
    public void setCompleted_at(LocalDateTime completed_at) { this.completed_at = completed_at; }
}
