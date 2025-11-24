package com.convertfile.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@WebServlet("/version")
public class VersionServlet extends HttpServlet {
    
    private String version;
    private String buildTime;
    private String deployTime;
    
    @Override
    public void init() throws ServletException {
        super.init();
        deployTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        try (InputStream input = getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF")) {
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
                version = props.getProperty("Implementation-Version", "Unknown");
                buildTime = props.getProperty("Build-Time", "Unknown");
            }
        } catch (Exception e) {
            version = "2.0.0";
            buildTime = "Unknown";
        }
        
        // Fallback: try pom.properties
        if ("Unknown".equals(version)) {
            try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("META-INF/maven/com.convertfile/CONVERT_FILE/pom.properties")) {
                if (input != null) {
                    Properties props = new Properties();
                    props.load(input);
                    version = props.getProperty("version", "2.0.0");
                }
            } catch (Exception e) {
                version = "2.0.0";
            }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = String.format(
            "{\"version\":\"%s\",\"buildTime\":\"%s\",\"deployTime\":\"%s\",\"javaVersion\":\"%s\",\"tomcatVersion\":\"%s\"}",
            version,
            buildTime,
            deployTime,
            System.getProperty("java.version"),
            getServletContext().getServerInfo()
        );
        
        response.getWriter().write(json);
    }
}
