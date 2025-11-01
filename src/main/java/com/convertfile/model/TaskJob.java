package com.convertfile.model;

import java.time.LocalDateTime;

public class TaskJob {
     //TODO: Fix cho phù hợp DATABASE
    private int id;
    private String fileId;
    private String jobType;
    private String status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public TaskJob() {
        this.fileId = "";
        this.jobType = "";
        this.status = "";
        this.message = "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TaskJob(String fileId, String jobType, String status, String message, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.fileId = fileId;
        this.jobType = jobType;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public TaskJob(TaskJob job){
        this.fileId = job.getFileId();
        this.jobType = job.getJobType();
        this.status = job.getStatus();
        this.message = job.getMessage();
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
