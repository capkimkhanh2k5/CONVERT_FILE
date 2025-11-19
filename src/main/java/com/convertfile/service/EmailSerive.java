package com.convertfile.service;

import java.util.Date;
import java.util.Properties;
import java.security.SecureRandom;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSerive  {
    
    // Gửi mail thông qua server của Gmail
    public static void sendEmail(String toAddress, String subject, String message) throws MessagingException {
        
        // 1. Cấu hình SMTP Server
        String host = "smtp.gmail.com";
        final String userName = PropertiesService.getEmailUsername();
        final String password = PropertiesService.getEmailPassword();

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // 2. Tạo session với xác thực
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // 3. Tạo nội dung email
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(message); // Gửi text thường. Nếu muốn gửi HTML dùng msg.setContent(htmlCode, "text/html");

        // 4. Gửi mail
        Transport.send(msg);
    }

    public static String generateOTP() {
        // Tạo mã OTP gồm 6 chữ số ngẫu nhiên
        SecureRandom secureRandom = new SecureRandom();
        int otpValue = 100000 + secureRandom.nextInt(900000);
        
        return String.valueOf(otpValue);
    }
}