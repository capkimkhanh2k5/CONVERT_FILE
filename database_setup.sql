-- ============================================
-- CONVERT_FILE DATABASE SETUP SCRIPT
-- Version: 2.0.0
-- Date: 2025-11-25
-- ============================================
-- This script sets up the complete database schema for the ConvertFile application
-- Run: Get-Content database_setup.sql | mysql -u root -pRmr2612+ -D file_converter
-- ============================================

USE file_converter;

-- ============================================
-- STEP 1: ADD INPUT_PUBLIC_ID COLUMN
-- ============================================
-- Track original uploaded file public_id for cleanup purposes
-- This allows deletion of both input and output files from Cloudinary

ALTER TABLE files 
ADD COLUMN IF NOT EXISTS input_public_id VARCHAR(255) NULL 
COMMENT 'Public ID of original uploaded file' 
AFTER public_id;

-- Create index for fast lookup
CREATE INDEX IF NOT EXISTS idx_input_public_id ON files(input_public_id);

SELECT '✅ Step 1: input_public_id column added' AS status;

-- ============================================
-- STEP 2: UPDATE TASK_TYPE ENUM
-- ============================================
-- Add all supported conversion types to task_type enum
-- Includes: DOCX, PDF, HTML, Markdown, PPTX, Images, Excel, CSV conversions

ALTER TABLE tasks 
MODIFY COLUMN task_type ENUM(
    'DOCX_TO_PDF',
    'PDF_TO_DOCX',
    'PPTX_TO_PDF',
    'CSV_TO_JSON',
    'DOCX_TO_XML',
    'XML_TO_DOCX',
    'DOCX_TO_HTML',
    'DOCX_TO_TXT',
    'DOCX_TO_MARKDOWN',
    'HTML_TO_MARKDOWN',
    'MARKDOWN_TO_HTML',
    'IMAGE_TO_PDF',
    'PDF_TO_IMAGE',
    'XLSX_TO_CSV',
    'IMG_FORMAT',
    'DOCX_MERGE',
    'UNKNOWN'
) NOT NULL;

SELECT '✅ Step 2: task_type enum updated with all conversion types' AS status;

-- ============================================
-- STEP 3: VERIFY SCHEMA
-- ============================================
-- Display current schema for verification

SELECT '📋 Files table structure:' AS info;
DESCRIBE files;

SELECT '📋 Tasks table structure:' AS info;
DESCRIBE tasks;

-- ============================================
-- STEP 4: DISPLAY SUMMARY
-- ============================================
SELECT 
    '✅ Database setup completed successfully!' AS status,
    NOW() AS completed_at;

SELECT 
    'Total conversion types supported: 17' AS info,
    'Cleanup feature: Enabled (guest files deleted on session end, user files after 24h)' AS features,
    'Session timeout: 30 minutes' AS settings;

-- ============================================
-- END OF SETUP SCRIPT
-- ============================================
