-- ============================================
-- CONVERT_FILE DATABASE SETUP SCRIPT
-- Version: 2.0.0
-- Date: 2025-11-25
-- ============================================
-- Complete database initialization script with latest schema updates
-- ============================================

-- 1. Xóa các bảng cũ trước (nếu có) để tránh lỗi
DROP TABLE IF EXISTS `tasks`;
DROP TABLE IF EXISTS `files`;
DROP TABLE IF EXISTS `users`;

-- 2. Tạo Database
CREATE DATABASE IF NOT EXISTS file_converter
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE file_converter;

-- ============================================
-- 3. Bảng users
-- ============================================
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` CHAR(60) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `picture_url` VARCHAR(255) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Users table indexes
CREATE INDEX `idx_username` ON `users` (`username`);
CREATE INDEX `idx_email` ON `users` (`email`);

-- ============================================
-- 4. Bảng files
-- ============================================
CREATE TABLE IF NOT EXISTS `files` (
    `file_id` VARCHAR(36) PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NULL,
    `original_name` VARCHAR(255) NOT NULL,
    `saved_name` VARCHAR(255) NOT NULL UNIQUE,
    `file_size` BIGINT UNSIGNED NOT NULL,
    `file_path` VARCHAR(1024) NULL COMMENT 'Cloudinary secure_url',
    `public_id` VARCHAR(255) NULL COMMENT 'Cloudinary public_id for deletion',
    `input_public_id` VARCHAR(255) NULL COMMENT 'Public ID of original uploaded file',
    `input_format` VARCHAR(20) NOT NULL,
    `output_format` VARCHAR(20) NULL,
    `current_status` ENUM('UPLOADED', 'PROCESSING', 'CONVERTED', 'FAILED', 'DELETED') NOT NULL DEFAULT 'UPLOADED',
    `description` VARCHAR(500) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Files table indexes (Basic + Performance Optimization)
CREATE INDEX `idx_user_id` ON `files` (`user_id`);
CREATE INDEX `idx_status_created_at` ON `files` (`current_status`, `created_at`);
CREATE INDEX `idx_public_id` ON `files` (`public_id`);
CREATE INDEX `idx_input_public_id` ON `files` (`input_public_id`);
CREATE INDEX `idx_files_user_created` ON `files` (`user_id`, `created_at`);
CREATE INDEX `idx_files_publicids` ON `files` (`file_id`, `public_id`, `input_public_id`);

-- ============================================
-- 5. Bảng tasks
-- ============================================
CREATE TABLE IF NOT EXISTS `tasks` (
    `task_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `file_id` VARCHAR(36) NOT NULL,
    `task_type` ENUM(
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
    ) NOT NULL,
    `status` ENUM('WAITING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELED') NOT NULL DEFAULT 'WAITING',
    `progress_percent` INT DEFAULT 0,
    `message` VARCHAR(1000) NULL,
    `worker_id` VARCHAR(50) NULL,
    `attempt_count` TINYINT UNSIGNED NOT NULL DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `started_at` DATETIME NULL,
    `completed_at` DATETIME NULL,
    FOREIGN KEY (`file_id`) REFERENCES `files`(`file_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tasks table indexes (Basic + Performance Optimization)
CREATE INDEX `idx_status` ON `tasks` (`status`);
CREATE INDEX `idx_file_id` ON `tasks` (`file_id`);
CREATE INDEX `idx_tasks_status_created` ON `tasks` (`status`, `created_at`);

-- ============================================
-- 6. Thêm dữ liệu mẫu (Mật khẩu mặc định là 123456)
-- ============================================
INSERT INTO users (user_id, username, password, email, created_at) 
VALUES (0, 'guest', 'N/A', 'guest@system.local', NOW());

INSERT INTO users (username, password, email, created_at) 
VALUES ('user01', '$2a$12$apGfQptjppS0PgqPNIa9PuRNaCnwq7bgifN1fXn7M8CFxcAnbf7ny', 'user01@example.com', NOW());

INSERT INTO users (username, password, email, created_at)   
VALUES ('admin', '$2a$12$apGfQptjppS0PgqPNIa9PuRNaCnwq7bgifN1fXn7M8CFxcAnbf7ny', 'admin@example.com', NOW());

INSERT INTO users (username, password, email, created_at) 
VALUES ('capkhanh', '$2a$12$apGfQptjppS0PgqPNIa9PuRNaCnwq7bgifN1fXn7M8CFxcAnbf7ny', 'capkhanh@gmail.com', NOW());

-- ============================================
-- SETUP SUMMARY
-- ============================================
-- Database: file_converter
-- Tables: users (4 sample users), files, tasks
-- Total conversion types: 17
-- Performance indexes: Optimized for fast queries
-- Cleanup feature: Enabled (guest files on session end, user files after 24h)
-- Session timeout: 30 minutes
-- ============================================