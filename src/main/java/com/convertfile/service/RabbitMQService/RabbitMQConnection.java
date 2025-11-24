package com.convertfile.service.RabbitMQService;

import com.convertfile.service.PropertiesService;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnection {

    public static final String QUEUE_NAME = "ConvertFileJSP";

    // Biến tĩnh để lưu kết nối duy nhất
    private static Connection connection = null;

    // Synchronized để an toàn khi nhiều luồng cùng gọi lúc khởi động
    public static synchronized Connection getConnection() throws Exception {
        // Nếu chưa có kết nối hoặc kết nối bị đứt thì mới tạo mới
        if (connection == null || !connection.isOpen()) {

            ConnectionFactory factory = new ConnectionFactory();

            String cloudUrl = PropertiesService.getRabbitMQUrl();

            if (cloudUrl == null || cloudUrl.isEmpty()) {
                throw new Exception("RabbitMQ URL is not configured inside application.properties");
            }

            factory.setUri(cloudUrl);

            // Xử lý fallback logic
            int connectionTimeout = PropertiesService.getRabbitMQConnectionTimeout();
            int requestedHeartbeat = PropertiesService.getRabbitMQRequestedHeartbeat();

            factory.setConnectionTimeout(connectionTimeout > 0 ? connectionTimeout : 30000);
            factory.setRequestedHeartbeat(requestedHeartbeat > 0 ? requestedHeartbeat : 30);

            // Tự động khôi phục kết nối nếu mạng chập chờn (Rất quan trọng)
            factory.setAutomaticRecoveryEnabled(true);

            System.out.println("--- Đang tạo mới kết nối tới RabbitMQ Cloud ---");
            connection = factory.newConnection();
        }

        return connection;
    }
}