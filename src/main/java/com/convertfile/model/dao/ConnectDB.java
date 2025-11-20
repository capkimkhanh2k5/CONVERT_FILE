package com.convertfile.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import com.convertfile.service.PropertiesService;

public class ConnectDB {

    private static String DB_URL;
    private static String USER;
    private static String PASS;

    public static Connection getConnection() {
        try {
            DB_URL = PropertiesService.getDatabaseUrl();
            USER = PropertiesService.getDatabaseUsername();
            PASS = PropertiesService.getDatabasePassword();

            // Fix cho Worker Thread: Set context classloader
            Thread currentThread = Thread.currentThread();
            ClassLoader originalClassLoader = currentThread.getContextClassLoader();

            try {
                // Đặt classloader thành classloader của ConnectDB class
                currentThread.setContextClassLoader(ConnectDB.class.getClassLoader());

                // 1. Nạp Driver thủ công (Để Worker Thread nhìn thấy)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // 2. Mở kết nối
                return DriverManager.getConnection(DB_URL, USER, PASS);

            } finally {
                // Khôi phục lại classloader ban đầu
                currentThread.setContextClassLoader(originalClassLoader);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}