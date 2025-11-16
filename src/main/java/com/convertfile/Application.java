package com.convertfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class Application {
    //Hàm main để khởi động Spring Boot -> Google OAuth2
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}