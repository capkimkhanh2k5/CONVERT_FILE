package com.convertfile.model.bean;

public class EnumStatus {
    public enum FileStatus {
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
        CANCELED,
    }

    public enum TaskType {
        DOCX_TO_PDF,
        PDF_TO_DOCX,
        CSV_TO_JSON,
        DOCX_TO_XML,
        XML_TO_DOCX,
        DOCX_TO_HTML,
        DOCX_TO_TXT,
        DOCX_TO_MARKDOWN,
        IMAGE_TO_PDF,
        PDF_TO_IMAGE,
        XLSX_TO_CSV,
        DOCX_MERGE,
        UNKNOWN
    }
}
