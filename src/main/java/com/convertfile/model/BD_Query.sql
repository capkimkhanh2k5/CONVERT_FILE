-- Tạo Database và cấu hình mặc định
CREATE DATABASE IF NOT EXISTS file_converter
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE file_converter;

-- Bảng users
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` CHAR(60) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chỉ mục cho bảng users
CREATE INDEX `idx_username` ON `users` (`username`);
CREATE INDEX `idx_email` ON `users` (`email`);


-- Bảng files
CREATE TABLE IF NOT EXISTS `files` (
    `file_id` VARCHAR(36) PRIMARY KEY, -- UUID
    `user_id` BIGINT UNSIGNED NULL,
    `original_name` VARCHAR(255) NOT NULL,
    `saved_name` VARCHAR(255) NOT NULL UNIQUE,
    `file_size` BIGINT UNSIGNED NOT NULL,
    `input_path` VARCHAR(1024) NOT NULL,
    `output_path` VARCHAR(1024) NULL,
    `input_format` VARCHAR(20) NOT NULL,
    `output_format` VARCHAR(20) NULL,
    `current_status` ENUM('UPLOADED', 'PROCESSING', 'CONVERTED', 'FAILED', 'DELETED') NOT NULL DEFAULT 'UPLOADED',
    `description` VARCHAR(500) NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chỉ mục cho bảng files
CREATE INDEX `idx_user_id` ON `files` (`user_id`);
CREATE INDEX `idx_status_created_at` ON `files` (`current_status`, `created_at`);
-- Chỉ mục cho saved_name đã được tạo bởi UNIQUE


-- Bảng tasks
CREATE TABLE IF NOT EXISTS `tasks` (
    `task_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `file_id` VARCHAR(36) NOT NULL,
    `task_type` ENUM('DOCX_TO_PDF', 'PDF_TO_DOCX', 'DOCX_TO_XML', 'XML_TO_DOCX', 'DOCX_TO_HTML', 'DOCX_MERGE', 'UNKNOWN') NOT NULL,
    `status` ENUM('WAITING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELED') NOT NULL DEFAULT 'WAITING',
    `message` VARCHAR(1000) NULL,
    `worker_id` VARCHAR(50) NULL,
    `attempt_count` TINYINT UNSIGNED NOT NULL DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `started_at` DATETIME NULL,
    `completed_at` DATETIME NULL,
    FOREIGN KEY (`file_id`) REFERENCES `files`(`file_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chỉ mục cho bảng tasks
CREATE INDEX `idx_status` ON `tasks` (`status`);
CREATE INDEX `idx_file_id` ON `tasks` (`file_id`);

DROP TABLE IF EXISTS `tasks`;
DROP TABLE IF EXISTS `files`;
DROP TABLE IF EXISTS `users`;