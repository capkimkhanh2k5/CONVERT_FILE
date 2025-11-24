-- Add new task types to support additional conversion services
ALTER TABLE tasks MODIFY COLUMN task_type ENUM(
    'DOCX_TO_PDF', 
    'PDF_TO_DOCX', 
    'CSV_TO_JSON',
    'DOCX_TO_XML', 
    'XML_TO_DOCX', 
    'DOCX_TO_HTML',
    'DOCX_TO_TXT',
    'DOCX_TO_MARKDOWN',
    'IMAGE_TO_PDF',
    'PDF_TO_IMAGE',
    'XLSX_TO_CSV',
    'DOCX_MERGE', 
    'UNKNOWN'
) NOT NULL;
