package com.convertfile.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import java.time.Duration;

/**
 * Redis Connection Manager (Phase 3)
 * 
 * Use cases:
 * 1. Cache: Task status, user sessions
 * 2. Pub/Sub: Broadcast WebSocket updates across servers
 * 3. Distributed Lock: Prevent duplicate processing
 * 
 * Architecture:
 * Worker-1 update task → Publish to Redis
 * → Redis broadcast to all Tomcat instances
 * → Each Tomcat push to connected WebSocket clients
 */
public class RedisConnect {
    
    private static final String HOST = "localhost";
    private static final int PORT = 6379;
    private static final int TIMEOUT = 3000; // 3 seconds
    private static final String PASSWORD = null; // No password by default
    
    // Pub/Sub channels
    public static final String TASK_UPDATE_CHANNEL = "task_updates";
    
    private static JedisPool jedisPool;
    
    /**
     * Initialize Redis connection pool
     */
    public static void initialize() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            return; // Already initialized
        }
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        
        // Pool size configuration
        poolConfig.setMaxTotal(20);                    // Max 20 connections
        poolConfig.setMaxIdle(10);                     // Max 10 idle
        poolConfig.setMinIdle(5);                      // Min 5 idle
        
        // Connection behavior
        poolConfig.setTestOnBorrow(true);              // Test before use
        poolConfig.setTestOnReturn(true);              // Test after use
        poolConfig.setTestWhileIdle(true);             // Test idle connections
        
        // Timeout settings
        poolConfig.setMaxWait(Duration.ofSeconds(3));  // Wait 3s for connection
        poolConfig.setMinEvictableIdleTime(Duration.ofMinutes(5)); // Close idle after 5 min
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMinutes(1)); // Check every 1 min
        
        // Create pool
        if (PASSWORD != null && !PASSWORD.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, HOST, PORT, TIMEOUT, PASSWORD);
        } else {
            jedisPool = new JedisPool(poolConfig, HOST, PORT, TIMEOUT);
        }
        
        // Test connection
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
            System.out.println("✅ Redis connection pool initialized");
            System.out.println("   Host: " + HOST + ":" + PORT);
            System.out.println("   Pool size: " + poolConfig.getMaxTotal());
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Redis: " + e.getMessage());
            System.err.println("⚠️  Continuing without Redis (degraded mode)");
        }
    }
    
    /**
     * Get Jedis connection from pool
     */
    public static Jedis getConnection() {
        if (jedisPool == null || jedisPool.isClosed()) {
            initialize();
        }
        return jedisPool.getResource();
    }
    
    /**
     * Cache task status (TTL 1 hour)
     * 
     * @param taskId Task ID
     * @param status Task status JSON
     */
    public static void cacheTaskStatus(long taskId, String status) {
        try (Jedis jedis = getConnection()) {
            String key = "task:" + taskId;
            jedis.setex(key, 3600, status); // Expire after 1 hour
        } catch (Exception e) {
            System.err.println("⚠️ Redis cache error: " + e.getMessage());
            // Continue without cache (graceful degradation)
        }
    }
    
    /**
     * Get cached task status
     * 
     * @param taskId Task ID
     * @return Task status JSON or null if not cached
     */
    public static String getCachedTaskStatus(long taskId) {
        try (Jedis jedis = getConnection()) {
            String key = "task:" + taskId;
            return jedis.get(key);
        } catch (Exception e) {
            System.err.println("⚠️ Redis cache error: " + e.getMessage());
            return null; // Fallback to database
        }
    }
    
    /**
     * Publish task update to all servers
     * 
     * @param taskId Task ID
     * @param status Status
     * @param progress Progress percent
     * @param message Message
     */
    public static void publishTaskUpdate(long taskId, String status, int progress, String message) {
        try (Jedis jedis = getConnection()) {
            // JSON message
            String json = String.format(
                "{\"taskId\":%d,\"status\":\"%s\",\"progress\":%d,\"message\":\"%s\"}",
                taskId, status, progress, message
            );
            
            // Publish to channel
            jedis.publish(TASK_UPDATE_CHANNEL, json);
            
            System.out.println("📡 Published task update to Redis: " + taskId);
        } catch (Exception e) {
            System.err.println("⚠️ Redis publish error: " + e.getMessage());
            // Continue without Redis (WebSocket on same server still works)
        }
    }
    
    /**
     * Subscribe to task updates (for WebSocket broadcast)
     * 
     * @param listener Callback for messages
     */
    public static void subscribeTaskUpdates(TaskUpdateListener listener) {
        new Thread(() -> {
            try (Jedis jedis = getConnection()) {
                System.out.println("🎧 Subscribed to Redis channel: " + TASK_UPDATE_CHANNEL);
                
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        listener.onTaskUpdate(message);
                    }
                }, TASK_UPDATE_CHANNEL);
                
            } catch (Exception e) {
                System.err.println("❌ Redis subscribe error: " + e.getMessage());
            }
        }, "Redis-Subscriber").start();
    }
    
    /**
     * Close pool gracefully
     */
    public static void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            System.out.println("🔌 Redis pool closed");
        }
    }
    
    /**
     * Get pool statistics
     */
    public static String getStats() {
        if (jedisPool == null || jedisPool.isClosed()) {
            return "Redis: Disconnected";
        }
        
        return String.format(
            "Redis: %s:%d, Active: %d, Idle: %d",
            HOST,
            PORT,
            jedisPool.getNumActive(),
            jedisPool.getNumIdle()
        );
    }
    
    /**
     * Listener interface for task updates
     */
    public interface TaskUpdateListener {
        void onTaskUpdate(String message);
    }
}
