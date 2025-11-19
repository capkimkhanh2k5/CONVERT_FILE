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
    public static void sendEmail(String toAddress, String subject, String messageHTML) throws MessagingException {
        
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

        msg.setContent(messageHTML, "text/html; charset=UTF-8");

        // 4. Gửi mail
        Transport.send(msg);
    }

    public static String getOtpEmail(String otpCode) {
        return "<!DOCTYPE html>"
        + "<html>"
        + "<head>"
        + "<meta charset='UTF-8'>"
        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
        + "<style>"
        + "  body {font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f6f6f6; margin: 0; padding: 0;}"
        + "  .email-wrapper {width: 100%; background-color: #f6f6f6; padding: 40px 0;}"
        + "  .email-content {max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); overflow: hidden;}"
        + "  .header {background: linear-gradient(90deg, #5e35b1, #00bcd4); padding: 30px; text-align: center;}"
        + "  .header h1 {color: #ffffff; margin: 0; font-size: 24px; font-weight: 600; letter-spacing: 1px;}"
        + "  .body-content {padding: 40px 30px; text-align: center; color: #333333;}"
        + "  .greeting {font-size: 18px; margin-bottom: 20px; color: #555555;}"
        + "  .message {font-size: 16px; line-height: 1.6; color: #666666; margin-bottom: 30px;}"
        + "  .otp-container {margin: 30px 0;}"
        + "  .otp-code {font-size: 32px; font-weight: 700; color: #2c3e50; background-color: #e8f0fe; padding: 15px 40px; border-radius: 6px; letter-spacing: 8px; display: inline-block; border: 1px solid #ccdfff;}"
        + "  .warning {font-size: 14px; color: #e74c3c; margin-top: 20px; font-weight: 500;}"
        + "  .footer {background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #999999; border-top: 1px solid #eeeeee;}"
        + "  .footer a {color: #2c3e50; text-decoration: none; font-weight: bold;}"
        + "</style>"
        + "</head>"
        + "<body>"
        + "  <div class='email-wrapper'>"
        + "    <div class='email-content'>"
        
        // --- HEADER ---
        + "      <div class='header'>"
        + "        <h1>ConvertFile DUT</h1>"
        + "      </div>"
        
        // --- BODY ---
        + "      <div class='body-content'>"
        + "        <p class='greeting'>Hello,</p>"
        + "        <p class='message'>You recently requested to verify your account for <strong>CONVERTFILE DUT</strong>.<br>Please use the code below to complete the process.</p>"
        
        // OTP BOX
        + "        <div class='otp-container'>"
        + "          <span class='otp-code'>" + otpCode + "</span>"
        + "        </div>"
        
        + "        <p class='message'>This verification code is valid for <strong>5 minutes</strong>.</p>"
        + "        <p class='warning'>If you did not request this code, please ignore this email.</p>"
        + "      </div>"
        
        // --- FOOTER ---
        + "      <div class='footer'>"
        + "        <p>&copy; 2025 CONVERTFILE DUT. All rights reserved.</p>"
        + "        <p>Need help? <a href='#'>Contact Support</a></p>"
        + "      </div>"
        
        + "    </div>"
        + "  </div>"
        + "</body>"
        + "</html>";
    }

    public static String generateOTP() {
        // Tạo mã OTP gồm 6 chữ số ngẫu nhiên
        SecureRandom secureRandom = new SecureRandom();
        int otpValue = 100000 + secureRandom.nextInt(900000);
        
        return String.valueOf(otpValue);
    }
}