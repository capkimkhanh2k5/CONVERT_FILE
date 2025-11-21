package com.convertfile.service.RabbitMQService;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.json.JSONObject;

public class RabbitMQService {

    public static void sendFileToQueue(int jobId, String filePath, String convertType) {
        // 1. Tạo nội dung JSON
        JSONObject json = new JSONObject();
        json.put("id", jobId);
        json.put("path", filePath);
        json.put("type", convertType);
        String message = json.toString();

        try {
            // 2. Lấy kết nối
            // Lấy ra ngoài block try-with-resources
            Connection connection = RabbitMQConnection.getConnection();

            // 3. Chỉ tạo và đóng Channel tự động
            try (Channel channel = connection.createChannel()) {

                // Khai báo hàng đợi (đảm bảo queue tồn tại)
                channel.queueDeclare(RabbitMQConnection.QUEUE_NAME, true, false, false, null);

                // Gửi tin nhắn
                channel.basicPublish("", RabbitMQConnection.QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));

                System.out.println(" [x] Đã gửi job ID " + jobId + " vào Queue");
            }
            // Kết thúc block này, Channel tự đóng, nhưng Connection vẫn sống.

        } catch (Exception e) {
            System.err.println("Lỗi gửi RabbitMQ: " + e.getMessage());
            e.printStackTrace();
            // TODO: Cập nhật DB status = ERROR ở đây để user biết
        }
    }
}