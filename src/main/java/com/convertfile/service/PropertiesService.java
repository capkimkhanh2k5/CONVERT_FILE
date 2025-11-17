package com.convertfile.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {
    private static Properties properties = new Properties();
    
    static {
        try (InputStream input = PropertiesService.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getGoogleClientId() {
        return properties.getProperty("google.client.id");
    }
    
    public static String getGoogleClientSecret() {
        return properties.getProperty("google.client.secret");
    }
}