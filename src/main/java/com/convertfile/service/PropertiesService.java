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

    public static String getEmailUsername() {
        return properties.getProperty("email.username");
    }

    public static String getEmailPassword() {
        return properties.getProperty("email.password");
    }

    public static String getDatabaseUrl() {
        return properties.getProperty("database.url");
    }

    public static String getDatabaseUsername() {
        return properties.getProperty("database.username");
    }

    public static String getDatabasePassword() {
        return properties.getProperty("database.password");
    }

    public static String getCloudinaryCloudName() {
        return properties.getProperty("cloudinary.cloud.name");
    }

    public static String getCloudinaryApiKey() {
        return properties.getProperty("cloudinary.api.key");
    }

    public static String getCloudinaryApiSecret() {
        return properties.getProperty("cloudinary.api.secret");
    }

    public static boolean getCloudinaryUrlSecure() {
        return Boolean.parseBoolean(properties.getProperty("cloudinary.url.secure", "true"));
    }

    public static int getRabbitMQConnectionTimeout() {
        return Integer.parseInt(properties.getProperty("rabbitmq.connectionTimeout", "30000"));
    }

    public static int getRabbitMQRequestedHeartbeat() {
        return Integer.parseInt(properties.getProperty("rabbitmq.requestedHeartbeat", "30"));
    }

    public static String getRabbitMQUrl() {
        return properties.getProperty("rabbitmq.url");
    }
}
