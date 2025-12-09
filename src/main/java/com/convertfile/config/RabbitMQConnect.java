package com.convertfile.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ Connection Manager (Phase 3)
 * 
 * Message Queue Architecture:
 * - Producers (Upload Servlet) → Push tasks to queue
 * - Consumers (Worker Pool) → Pull tasks from queue
 * - Decoupling: Web server ≠ Worker server
 * 
 * Benefits:
 * - Scalable: Add more workers without code change
 * - Reliable: Tasks persisted, survive server restart
 * - Load balancing: Workers auto-balance workload
 * - Dead Letter Queue: Failed messages after 3 retries go to DLQ
 */
public class RabbitMQConnect {

    // ✅ SECURITY: Load credentials from PropertiesService (not hardcoded)
    private static final String RABBITMQ_URL = com.convertfile.service.PropertiesService.getRabbitMQUrl();
    private static final String VIRTUAL_HOST = com.convertfile.service.PropertiesService
            .getProperty("rabbitmq.virtualHost");

    // Queue names
    public static final String TASK_QUEUE = "file_conversion_tasks";
    public static final String EXCHANGE_NAME = "file_conversion_exchange";
    public static final String ROUTING_KEY = "task.new";

    // Dead Letter Queue configuration
    public static final String DEAD_LETTER_QUEUE = "file_conversion_dlq";
    public static final String DEAD_LETTER_EXCHANGE = "file_conversion_dlx";

    private static Connection connection;
    private static Channel channel;

    /**
     * Initialize RabbitMQ connection
     */
    public static void initialize() throws IOException, TimeoutException {
        if (connection != null && connection.isOpen()) {
            return; // Already initialized
        }

        ConnectionFactory factory = new ConnectionFactory();

        // ✅ Use AMQP URL from properties (includes credentials securely)
        try {
            factory.setUri(RABBITMQ_URL);
        } catch (Exception e) {
            System.err.println("❌ Invalid RabbitMQ URL, falling back to localhost");
            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
        }

        // Override virtual host if specified
        if (VIRTUAL_HOST != null && !VIRTUAL_HOST.isEmpty()) {
            factory.setVirtualHost(VIRTUAL_HOST);
        }

        // Connection pooling settings
        factory.setConnectionTimeout(30000); // 30s timeout
        factory.setRequestedHeartbeat(60); // 60s heartbeat
        factory.setAutomaticRecoveryEnabled(true); // Auto-reconnect
        factory.setNetworkRecoveryInterval(10000); // Retry every 10s

        connection = factory.newConnection("FileConverter-Producer");
        channel = connection.createChannel();

        // ✅ Dead Letter Exchange for failed messages
        channel.exchangeDeclare(
                DEAD_LETTER_EXCHANGE,
                "direct", // Type: direct routing
                true, // Durable: survive broker restart
                false, // Auto-delete: no
                null // Arguments
        );

        // ✅ Dead Letter Queue
        channel.queueDeclare(
                DEAD_LETTER_QUEUE,
                true, // Durable
                false, // Exclusive: no
                false, // Auto-delete: no
                null // Arguments
        );
        channel.queueBind(DEAD_LETTER_QUEUE, DEAD_LETTER_EXCHANGE, "dead");

        // Declare main exchange (topic type for routing)
        channel.exchangeDeclare(
                EXCHANGE_NAME,
                "topic", // Type: topic (routing based on key)
                true, // Durable: survive broker restart
                false, // Auto-delete: no
                null // Arguments
        );

        // ✅ Declare main queue with DLQ configuration
        java.util.Map<String, Object> queueArgs = new java.util.HashMap<>();
        queueArgs.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        queueArgs.put("x-dead-letter-routing-key", "dead");

        channel.queueDeclare(
                TASK_QUEUE,
                true, // Durable: messages survive restart
                false, // Exclusive: multiple consumers allowed
                false, // Auto-delete: no
                queueArgs // ✅ DLQ arguments
        );

        // Bind queue to exchange
        channel.queueBind(TASK_QUEUE, EXCHANGE_NAME, ROUTING_KEY);

        System.out.println("✅ RabbitMQ connection initialized");
        System.out.println("   Exchange: " + EXCHANGE_NAME);
        System.out.println("   Queue: " + TASK_QUEUE);
        System.out.println("   Dead Letter Queue: " + DEAD_LETTER_QUEUE);
        System.out.println("   Routing Key: " + ROUTING_KEY);
    }

    /**
     * Get channel for publishing/consuming
     */
    public static Channel getChannel() throws IOException, TimeoutException {
        if (channel == null || !channel.isOpen()) {
            initialize();
        }
        return channel;
    }

    /**
     * Publish task to queue
     * 
     * @param taskId Task ID to process
     * @throws IOException if publish fails
     */
    public static void publishTask(long taskId) throws IOException, TimeoutException {
        Channel ch = getChannel();

        // Message body: just task ID as string
        String message = String.valueOf(taskId);

        // Publish with persistence
        ch.basicPublish(
                EXCHANGE_NAME,
                ROUTING_KEY,
                com.rabbitmq.client.MessageProperties.PERSISTENT_TEXT_PLAIN, // Persist message
                message.getBytes("UTF-8"));

        System.out.println("📤 Published task " + taskId + " to RabbitMQ");
    }

    /**
     * Create consumer channel (for workers)
     */
    public static Channel createConsumerChannel() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            initialize();
        }

        Channel consumerChannel = connection.createChannel();

        // Set prefetch count: worker can handle 5 messages at a time
        // Allows parallel processing with 5 worker threads
        // Each unacknowledged message will be assigned to available worker
        consumerChannel.basicQos(5);

        return consumerChannel;
    }

    /**
     * Close connection gracefully
     */
    public static void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                System.out.println("🔌 RabbitMQ channel closed");
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
                System.out.println("🔌 RabbitMQ connection closed");
            }
        } catch (IOException | TimeoutException e) {
            System.err.println("❌ Error closing RabbitMQ connection: " + e.getMessage());
        }
    }

    /**
     * Get connection statistics
     */
    public static String getStats() {
        if (connection == null || !connection.isOpen()) {
            return "RabbitMQ: Disconnected";
        }

        return String.format(
                "RabbitMQ: Connected to %s, Channel: %s",
                connection.getAddress().getHostAddress(),
                channel != null && channel.isOpen() ? "Open" : "Closed");
    }
}
