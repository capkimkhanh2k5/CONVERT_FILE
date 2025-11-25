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
            + "  body {font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;}"
            + "  .email-wrapper {width: 100%; background-color: #f4f4f4; padding: 40px 0;}"
            + "  .email-content {max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); overflow: hidden;}"
            
            // HEADER STYLE: Nền trắng để làm nổi bật chữ Gradient
            + "  .header {padding: 30px 20px; text-align: center; border-bottom: 1px solid #eeeeee;}"
            
            // LOGO STYLE: Gradient Text
            + "  .logo-text {font-size: 28px; font-weight: 800; margin: 0; letter-spacing: -0.5px;"
            + "    background: linear-gradient(90deg, #5e35b1, #00bcd4);" // Màu chuyển bạn yêu cầu
            + "    -webkit-background-clip: text;"
            + "    -webkit-text-fill-color: transparent;"
            + "    color: #5e35b1; display: inline-block;}" // Fallback color (Tím) cho Outlook
            
            + "  .header-sub {font-size: 14px; color: #888888; margin-top: 5px; text-transform: uppercase; letter-spacing: 1px;}"
            + "  .body-content {padding: 40px 30px; text-align: center; color: #333333;}"
            + "  .greeting {font-size: 18px; margin-bottom: 20px; color: #444444;}"
            + "  .message {font-size: 16px; line-height: 1.6; color: #666666; margin-bottom: 30px;}"
            
            // OTP BOX STYLE
            + "  .otp-container {margin: 30px 0;}"
            + "  .otp-code {font-size: 36px; font-weight: 700; color: #5e35b1; background-color: #f3e5f5; padding: 15px 40px; border-radius: 8px; letter-spacing: 8px; display: inline-block; border: 1px dashed #5e35b1;}"
            
            + "  .warning {font-size: 13px; color: #e74c3c; margin-top: 25px; font-style: italic;}"
            + "  .footer {background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #999999; border-top: 1px solid #eeeeee;}"
            + "</style>"
            + "</head>"
            + "<body>"
            + "  <div class='email-wrapper'>"
            + "    <div class='email-content'>"
            
            // --- HEADER VỚI GRADIENT TEXT ---
            + "      <div class='header'>"
            + "        <h1 class='logo-text'>CONVERTFILE DUT</h1>"
            + "        <div class='header-sub'>Authentication Service</div>"
            + "      </div>"
            
            // --- BODY ---
            + "      <div class='body-content'>"
            + "        <p class='greeting'>Hello,</p>"
            + "        <p class='message'>We received a request to verify your email address. Please use the One-Time Password (OTP) below to proceed.</p>"
            
            // OTP CODE
            + "        <div class='otp-container'>"
            + "          <span class='otp-code'>" + otpCode + "</span>"
            + "        </div>"
            
            + "        <p class='message'>This code is valid for <strong>5 minutes</strong>.</p>"
            + "        <p class='warning'>If you did not initiate this request, please ignore this email immediately.</p>"
            + "      </div>"
            
            // --- FOOTER ---
            + "      <div class='footer'>"
            + "        <p>&copy; 2025 CONVERTFILE DUT. All rights reserved.</p>"
            + "        <p>Automated message, please do not reply.</p>"
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
