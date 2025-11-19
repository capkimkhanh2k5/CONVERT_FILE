package com.convertfile.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {
    // Cấu hình XAMPP (User: root, Pass: rỗng)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/file_converter?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static Connection getConnection() {
        try {
            // 1. Nạp Driver thủ công (Để Worker Thread nhìn thấy)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Mở kết nối
            return DriverManager.getConnection(DB_URL, USER, PASS);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}