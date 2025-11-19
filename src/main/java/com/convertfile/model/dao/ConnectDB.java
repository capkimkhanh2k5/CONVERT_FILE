package com.convertfile.model.dao;

import java.sql.Connection;
import java.sql.Driver; 
import java.util.Properties;

import com.convertfile.service.PropertiesService;


public class ConnectDB {
    private static String DB_URL = "";
    private static String USER = "";
    private static String PASS = ""; 

    public static Connection getConnection() {
        try {
            Driver driver = new com.mysql.cj.jdbc.Driver();

            DB_URL = PropertiesService.getDatabaseUrl();
            USER = PropertiesService.getDatabaseUsername();
            PASS = PropertiesService.getDatabasePassword();
            
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