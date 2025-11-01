package com.convertfile.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    //TODO: Chỉnh lại cho đúng khi có DB
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/file_converter";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public static Connection getConnection() throws SQLException {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error connect DataBase: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Error connect DataBase: " + e.getMessage());
        }
        
        return conn;
    }

}
