package com.convertfile.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * HikariCP Connection Pool Manager (Phase 2)
 * 
 * BEFORE: Mỗi DAO tạo connection mới → 5 workers × 3 calls = 15 connections
 * AFTER: Pool 20 connections, tái sử dụng → hiệu quả hơn 10x
 * 
 * Configuration:
 * - Maximum Pool Size: 20 connections
 * - Minimum Idle: 5 connections (luôn sẵn sàng)
 * - Connection Timeout: 30s
 * - Idle Timeout: 10 minutes (đóng connection không dùng)
 * - Max Lifetime: 30 minutes (refresh connection định kỳ)
 */
public class DBConnect {
    
    private static HikariDataSource dataSource;
    
    // Database credentials (giống application.properties)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/file_converter?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Rmr2612+";
    
    static {
        try {
            initializePool();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize HikariCP", e);
        }
    }
    
    /**
     * Khởi tạo HikariCP connection pool
     */
    private static void initializePool() {
        HikariConfig config = new HikariConfig();
        
        // Database connection settings
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Pool size configuration
        config.setMaximumPoolSize(20);        // Max 20 connections (5 workers × 4 connections/worker)
        config.setMinimumIdle(5);             // Luôn giữ 5 connections sẵn sàng
        
        // Timeout settings
        config.setConnectionTimeout(30000);   // 30 seconds để lấy connection từ pool
        config.setIdleTimeout(600000);        // 10 minutes - đóng connection idle
        config.setMaxLifetime(1800000);       // 30 minutes - refresh connection định kỳ
        
        // Performance tuning
        config.setAutoCommit(true);           // Auto-commit mỗi query
        config.setConnectionTestQuery("SELECT 1"); // Test query để verify connection
        
        // Pool name for debugging
        config.setPoolName("FileConverter-HikariPool");
        
        // Logging
        config.setLeakDetectionThreshold(60000); // Cảnh báo nếu connection không return sau 60s
        
        // MySQL-specific optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        dataSource = new HikariDataSource(config);
        
        System.out.println("✅ HikariCP Connection Pool initialized:");
        System.out.println("   - Max Pool Size: " + config.getMaximumPoolSize());
        System.out.println("   - Min Idle: " + config.getMinimumIdle());
        System.out.println("   - Connection Timeout: " + config.getConnectionTimeout() + "ms");
    }
    
    /**
     * Lấy connection từ pool
     * 
     * USAGE in DAO:
     * try (Connection conn = DBConnect.getConnection()) {
     *     // Execute query
     * } // Auto-return connection to pool
     * 
     * @return Connection từ pool
     * @throws SQLException nếu pool hết connection hoặc timeout
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("HikariCP not initialized");
        }
        
        Connection conn = dataSource.getConnection();
        
        // Debug: Log active connections
        if (dataSource.getHikariPoolMXBean().getActiveConnections() > 15) {
            System.err.println("⚠️ High connection usage: " + 
                dataSource.getHikariPoolMXBean().getActiveConnections() + "/20 active");
        }
        
        return conn;
    }
    
    /**
     * Đóng connection pool (gọi khi shutdown app)
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🛑 HikariCP Connection Pool closed");
        }
    }
    
    /**
     * Get pool statistics (for monitoring)
     */
    public static String getPoolStats() {
        if (dataSource == null) return "Pool not initialized";
        
        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
}
