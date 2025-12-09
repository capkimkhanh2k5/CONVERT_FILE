package com.convertfile.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized Logging Utility for ConvertFile Application
 * 
 * USAGE:
 * LoggerUtil.info(this.getClass(), "Task {} started", taskId);
 * LoggerUtil.error(this.getClass(), "Failed", exception);
 * 
 * WHY:
 * - SLF4J facade allows switching backends (Logback, Log4j2)
 * - Structured logging with parameterized messages
 * - Centralized control over log levels
 */
public final class LoggerUtil {

    private LoggerUtil() {
        // Utility class - no instantiation
    }

    /**
     * Get logger for a specific class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    // ========== CONVENIENCE METHODS ==========

    public static void debug(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).debug(message, args);
    }

    public static void info(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).info(message, args);
    }

    public static void warn(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).warn(message, args);
    }

    public static void error(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).error(message, args);
    }

    public static void error(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).error(message, throwable);
    }

    // ========== SPECIALIZED LOGGING ==========

    /**
     * Log task processing events with consistent format
     */
    public static void task(Class<?> clazz, long taskId, String event) {
        getLogger(clazz).info("[Task-{}] {}", taskId, event);
    }

    /**
     * Log RabbitMQ events
     */
    public static void rabbitmq(Class<?> clazz, String event, Object... args) {
        getLogger(clazz).info("[RabbitMQ] " + event, args);
    }

    /**
     * Log Cloudinary events
     */
    public static void cloud(Class<?> clazz, String event, Object... args) {
        getLogger(clazz).info("[Cloudinary] " + event, args);
    }
}
