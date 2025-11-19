package com.convertfile.model.dao;

import java.sql.Connection;
import java.sql.Driver; 
import java.sql.SQLException;
import java.util.Properties;

public class ConnectDB {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/file_converter?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static Connection getConnection() {
        try {
            // ĐÂY LÀ DÒNG QUAN TRỌNG NHẤT ĐỂ SỬA LỖI CỦA BẠN
            Driver driver = new com.mysql.cj.jdbc.Driver();
            
            Properties props = new Properties();
            props.put("user", USER);
            props.put("password", PASS);

            return driver.connect(DB_URL, props);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}