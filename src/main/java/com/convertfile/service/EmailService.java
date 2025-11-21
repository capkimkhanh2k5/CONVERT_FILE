package com.convertfile.service;

import java.util.Date;
import java.util.Properties;
import java.security.SecureRandom;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

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

    // Helper method to create a consistent, modern email template
    private static String createEmailTemplate(String title, String subTitle, String contentBody) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<style>"
                + "  body { margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; }"
                + "  .email-container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }"
                + "  .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 20px; text-align: center; color: #ffffff; }"
                + "  .header h1 { margin: 0; font-size: 28px; font-weight: 700; letter-spacing: 1px; }"
                + "  .header p { margin: 10px 0 0; font-size: 14px; opacity: 0.9; font-weight: 300; text-transform: uppercase; letter-spacing: 2px; }"
                + "  .content { padding: 40px 30px; color: #333333; line-height: 1.6; }"
                + "  .greeting { font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #2d3748; }"
                + "  .message { margin-bottom: 20px; color: #4a5568; }"
                + "  .otp-box { background-color: #f0f4f8; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0; border: 2px dashed #764ba2; }"
                + "  .otp-code { font-size: 32px; font-weight: bold; color: #764ba2; letter-spacing: 8px; display: inline-block; }"
                + "  .alert-box { background-color: #fff5f5; border-left: 4px solid #e53e3e; color: #c53030; padding: 15px; border-radius: 4px; margin: 20px 0; }"
                + "  .footer { background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; color: #a0aec0; border-top: 1px solid #edf2f7; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "  <div class='email-container'>"
                + "    <div class='header'>"
                + "      <h1>" + title + "</h1>"
                + "      <p>" + subTitle + "</p>"
                + "    </div>"
                + "    <div class='content'>"
                + contentBody
                + "    </div>"
                + "    <div class='footer'>"
                + "      <p>&copy; 2025 ConvertFile DUT. All rights reserved.</p>"
                + "      <p>Automated message, please do not reply.</p>"
                + "    </div>"
                + "  </div>"
                + "</body>"
                + "</html>";
    }

    public static String getOtpEmail(String otpCode) {
        String contentBody = "      <p class='greeting'>Hello,</p>"
                + "      <p class='message'>We received a request to verify your email address. Please use the One-Time Password (OTP) below to proceed.</p>"
                + "      <div class='otp-box'>"
                + "        <span class='otp-code'>" + otpCode + "</span>"
                + "      </div>"
                + "      <p class='message'>This code is valid for <strong>5 minutes</strong>.</p>"
                + "      <p class='message' style='font-size: 13px; color: #718096;'>If you did not initiate this request, please ignore this email.</p>";

        return createEmailTemplate("ConvertFile", "Authentication Service", contentBody);
    }

    public static String generateOTP() {
        // Tạo mã OTP gồm 6 chữ số ngẫu nhiên
        SecureRandom secureRandom = new SecureRandom();
        int otpValue = 100000 + secureRandom.nextInt(900000);

        return String.valueOf(otpValue);
    }

    public static String sendOtp(String email) throws MessagingException {
        String otp = generateOTP();
        String subject = "Your OTP Code - ConvertFile";
        String messageHTML = getOtpEmail(otp);
        sendEmail(email, subject, messageHTML);
        return otp;
    }

    public static void sendPasswordChangeWarning(String email) throws MessagingException {
        String subject = "Security Alert: Password Changed - ConvertFile";

        String contentBody = "      <p class='greeting'>Security Alert</p>"
                + "      <p class='message'>The password for your ConvertFile account was recently changed.</p>"
                + "      <div class='alert-box'>"
                + "        <strong>Warning:</strong> If you did not make this change, please contact support immediately."
                + "      </div>"
                + "      <p class='message'>If you made this change, you can safely ignore this email.</p>";

        String messageHTML = createEmailTemplate("Security Alert", "Account Protection", contentBody);

        sendEmail(email, subject, messageHTML);
    }
}