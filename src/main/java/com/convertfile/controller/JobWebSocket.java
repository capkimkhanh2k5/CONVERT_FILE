package com.convertfile.controller;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

/**
 * WebSocket Endpoint for Real-time Job Updates (Phase 2)
 * 
 * BEFORE (Phase 1): Client polling every 2-5 seconds
 * - 100 users × 12 requests/min = 1200 requests/min
 * - High server load
 * - Delay 2-5 seconds
 * 
 * AFTER (Phase 2): WebSocket push
 * - 100 users = 100 persistent connections
 * - Server push updates instantly
 * - 0 polling requests
 * - Real-time (0ms delay)
 * 
 * URL: ws://localhost:8080/CONVERT_FILE/ws/jobs
 * 
 * Usage:
 * const ws = new WebSocket('ws://localhost:8080/CONVERT_FILE/ws/jobs');
 * ws.onmessage = (event) => {
 *     const data = JSON.parse(event.data);
 *     console.log('Task update:', data);
 * };
 */
@ServerEndpoint("/ws/jobs")
public class JobWebSocket {
    
    // Thread-safe set of all connected sessions
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("✅ WebSocket connected: " + session.getId() + 
                          " (Total: " + sessions.size() + " sessions)");
        
        // Send welcome message
        try {
            Map<String, Object> welcome = Map.of(
                "type", "connection",
                "message", "WebSocket connected successfully",
                "sessionId", session.getId()
            );
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(welcome));
        } catch (IOException e) {
            System.err.println("❌ Error sending welcome message: " + e.getMessage());
        }
    }
    
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("🔌 WebSocket disconnected: " + session.getId() + 
                          " (Remaining: " + sessions.size() + " sessions)");
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("❌ WebSocket error on session " + session.getId() + ": " + 
                          error.getMessage());
        error.printStackTrace();
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("📨 WebSocket message from " + session.getId() + ": " + message);
        
        // Optional: Handle client messages (e.g., ping/pong, subscribe to specific jobs)
        try {
            Map<?, ?> data = objectMapper.readValue(message, Map.class);
            String type = (String) data.get("type");
            
            if ("ping".equals(type)) {
                Map<String, Object> pong = Map.of(
                    "type", "pong",
                    "timestamp", System.currentTimeMillis()
                );
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(pong));
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error handling message: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast task update to all connected clients
     * 
     * Called by FileWorker when task status changes:
     * JobWebSocket.broadcastTaskUpdate(taskId, status, progress, message);
     * 
     * @param taskId Task ID
     * @param status Task status (WAITING, PROCESSING, COMPLETED, FAILED)
     * @param progress Progress percentage (0-100)
     * @param message Status message
     */
    public static void broadcastTaskUpdate(long taskId, String status, int progress, String message) {
        Map<String, Object> update = Map.of(
            "type", "task_update",
            "taskId", taskId,
            "status", status,
            "progress", progress,
            "message", message,
            "timestamp", System.currentTimeMillis()
        );
        
        broadcast(update);
    }
    
    /**
     * Broadcast task update with file info
     * 
     * @param taskId Task ID
     * @param fileId File ID
     * @param status Task status
     * @param progress Progress percentage
     * @param message Status message
     * @param downloadUrl Download URL (if completed)
     */
    public static void broadcastTaskUpdateWithFile(
        long taskId, String fileId, String status, int progress, 
        String message, String downloadUrl
    ) {
        Map<String, Object> update = Map.of(
            "type", "task_update",
            "taskId", taskId,
            "fileId", fileId,
            "status", status,
            "progress", progress,
            "message", message,
            "downloadUrl", downloadUrl != null ? downloadUrl : "",
            "timestamp", System.currentTimeMillis()
        );
        
        broadcast(update);
    }
    
    /**
     * Broadcast generic message to all clients
     */
    private static void broadcast(Map<String, Object> data) {
        if (sessions.isEmpty()) {
            return; // No clients connected
        }
        
        try {
            String jsonMessage = objectMapper.writeValueAsString(data);
            
            // Send to all connected sessions
            synchronized (sessions) {
                for (Session session : sessions) {
                    if (session.isOpen()) {
                        try {
                            session.getBasicRemote().sendText(jsonMessage);
                        } catch (IOException e) {
                            System.err.println("❌ Error sending to session " + session.getId() + 
                                             ": " + e.getMessage());
                        }
                    }
                }
            }
            
            System.out.println("📡 Broadcast to " + sessions.size() + " clients: " + 
                             data.get("type"));
            
        } catch (Exception e) {
            System.err.println("❌ Error broadcasting message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get number of connected clients
     */
    public static int getConnectedClients() {
        return sessions.size();
    }
    
    /**
     * Phase 3: Broadcast task update from Redis Pub/Sub
     * 
     * Called by RedisConnect when receiving message from other servers:
     * Redis Pub/Sub → RedisConnect listener → This method → WebSocket clients
     * 
     * This enables cross-server WebSocket synchronization:
     * - Server 1 worker updates task → Publish to Redis
     * - Server 2 RedisConnect receives → Calls this method
     * - Server 2 WebSocket clients get update
     * 
     * @param taskId Task ID
     * @param status Task status
     * @param progress Progress percentage
     * @param message Status message
     */
    public static void broadcastFromRedis(long taskId, String status, int progress, String message) {
        System.out.println("📡 [Redis→WebSocket] Broadcasting task " + taskId + " from Redis");
        broadcastTaskUpdate(taskId, status, progress, message);
    }
}
