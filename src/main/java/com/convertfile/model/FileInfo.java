package com.convertfile.model;

import java.time.LocalDateTime;

public class FileInfo {
    //TODO: Fix cho phù hợp DATABASE
    private int id; //Auto
    private String fileId;
    private String originalName;
    private String savedName;
    private double fileSize;
    private String status;
    private String filePath;
    private LocalDateTime uploadTime;

    //Constructor
    public FileInfo() {
        this.fileId = "";
        this.originalName = "";
        this.savedName = "";
        this.fileSize = 0.0;
        this.status = "";
        this.filePath = "";
        this.uploadTime = LocalDateTime.now();
    }

    public FileInfo(String fileId, String originalName, String savedName, double fileSize, String status, String filePath, LocalDateTime uploadTime) {
        this.fileId = fileId;
        this.originalName = originalName;
        this.savedName = savedName;
        this.fileSize = fileSize;
        this.status = status;
        this.filePath = filePath;
        this.uploadTime = uploadTime;
    }

    public FileInfo(FileInfo info){
        this.fileId = info.getFileId();
        this.originalName = info.getOriginalName();
        this.savedName = info.getSavedName();
        this.fileSize = info.getFileSize();
        this.status = info.getStatus();
        this.filePath = info.getFilePath();
        this.uploadTime = info.getUploadTime();
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getSavedName() { return savedName; }
    public void setSavedName(String savedName) { this.savedName = savedName; }

    public double getFileSize() { return fileSize; }
    public void setFileSize(double fileSize) { this.fileSize = fileSize; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
}
