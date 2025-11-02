package com.convertfile.model;

public class EnumStatus {
    public enum FileStatus{
        UPLOADED,
        PROCESSING,
        CONVERTED,
        FAILED,
        DELETED
    }

    public enum TaskStatus {
        WAITING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELED
    }

    //TODO: Chưa hoàn thiện, bổ sung khi làm đủ
    public enum TaskType {
        CONVERT_PDF_TO_DOCX,
        CONVERT_DOCX_TO_PDF,
        CONVERT_JPG_TO_PNG,
        CONVERT_PNG_TO_JPG
    }
}
