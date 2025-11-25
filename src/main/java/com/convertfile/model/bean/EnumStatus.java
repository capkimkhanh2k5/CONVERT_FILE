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
        PPTX_TO_PDF,
        CSV_TO_JSON,
        DOCX_TO_XML,
        XML_TO_DOCX,
        DOCX_TO_HTML,
        DOCX_TO_TXT,
        DOCX_TO_MARKDOWN,
        HTML_TO_MARKDOWN,
        MARKDOWN_TO_HTML,
        IMAGE_TO_PDF,
        IMG_FORMAT,
        PDF_TO_IMAGE,
        XLSX_TO_CSV,
        DOCX_MERGE,
        UNKNOWN
    }
}
