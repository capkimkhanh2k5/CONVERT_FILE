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

            // Driver đã được load ở AppListener, chỉ cần tạo connection
            return DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (Exception e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}