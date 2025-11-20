package com.convertfile.service;

import jakarta.servlet.http.HttpSession;
import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized OTP Service for managing One-Time Password operations
 * Used by both Registration and Forgot Password features
 */
public class OTPService {

    // Configuration constants
    private static final long OTP_EXPIRY_MILLIS = 5 * 60 * 1000; // 5 minutes
    private static final long RATE_LIMIT_MILLIS = 60000; // 1 minute between requests
    private static final int MAX_VERIFICATION_ATTEMPTS = 5;

    // Session attribute keys for Registration OTP
    private static final String REG_OTP_KEY = "registration_otp";
    private static final String REG_EMAIL_KEY = "registration_email";
    private static final String REG_TIMESTAMP_KEY = "otp_timestamp";
    private static final String REG_ATTEMPTS_KEY = "otp_attempts";
    private static final String REG_LAST_REQUEST_KEY = "last_otp_request_time";

    // Session attribute keys for Forgot Password OTP
    private static final String FORGOT_OTP_KEY = "forgot_password_otp";
    private static final String FORGOT_EMAIL_KEY = "forgot_password_email";
    private static final String FORGOT_TIMESTAMP_KEY = "forgot_password_timestamp";
    private static final String FORGOT_ATTEMPTS_KEY = "forgot_password_attempts";
    private static final String FORGOT_LAST_REQUEST_KEY = "forgot_password_last_request";

    /**
     * OTP Type Enum to differentiate between Registration and Forgot Password
     */
    public enum OTPType {
        REGISTRATION,
        FORGOT_PASSWORD
    }

    /**
     * Result class for OTP operations
     */
    public static class OTPResult {
        private boolean success;
        private String message;
        private Map<String, Object> data;

        public OTPResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.data = new HashMap<>();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void addData(String key, Object value) {
            this.data.put(key, value);
        }
    }

    /**
     * Send OTP to email
     */
    public static OTPResult sendOTP(HttpSession session, String email, OTPType type) {
        try {
            // Check rate limiting
            String lastRequestKey = getLastRequestKey(type);
            Long lastRequestTime = (Long) session.getAttribute(lastRequestKey);

            if (lastRequestTime != null) {
                long timeSinceLastRequest = System.currentTimeMillis() - lastRequestTime;
                if (timeSinceLastRequest < RATE_LIMIT_MILLIS) {
                    long remainingSeconds = (RATE_LIMIT_MILLIS - timeSinceLastRequest) / 1000;
                    return new OTPResult(false,
                            "Please wait " + remainingSeconds + " seconds before requesting another OTP");
                }
            }

            // Generate and send OTP
            String otp = EmailService.generateOTP();
            String subject = type == OTPType.REGISTRATION
                    ? "Email Verification - ConvertFile"
                    : "Password Reset Code - ConvertFile";
            String htmlContent = EmailService.getOtpEmail(otp);

            EmailService.sendEmail(email, subject, htmlContent);

            // Hash OTP before storing in session (Security improvement)
            String hashedOtp = passwordService.hassPassword(otp);

            // Store hashed OTP in session
            String otpKey = getOTPKey(type);
            String emailKey = getEmailKey(type);
            String timestampKey = getTimestampKey(type);
            String attemptsKey = getAttemptsKey(type);

            session.setAttribute(otpKey, hashedOtp);
            session.setAttribute(emailKey, email);
            session.setAttribute(timestampKey, System.currentTimeMillis());
            session.setAttribute(attemptsKey, 0);
            session.setAttribute(lastRequestKey, System.currentTimeMillis());

            // Log with masked email (Privacy improvement)
            System.out.println("[OTP-" + type + "] Sent to: " + maskEmail(email) + " at " + new java.util.Date());

            OTPResult result = new OTPResult(true, "OTP sent successfully");
            result.addData("expiryMinutes", OTP_EXPIRY_MILLIS / 60000);
            return result;

        } catch (MessagingException e) {
            System.err.println("[OTP-" + type + "] Failed to send: " + e.getMessage());
            e.printStackTrace();
            return new OTPResult(false, "Failed to send OTP. Please try again.");
        }
    }

    /**
     * Verify OTP
     */
    public static OTPResult verifyOTP(HttpSession session, String inputOtp, OTPType type) {
        // Validate OTP format
        if (inputOtp == null || !inputOtp.matches("^\\d{6}$")) {
            return new OTPResult(false, "Invalid OTP format. Must be 6 digits.");
        }

        // Get session data
        String otpKey = getOTPKey(type);
        String timestampKey = getTimestampKey(type);
        String attemptsKey = getAttemptsKey(type);

        String storedOtp = (String) session.getAttribute(otpKey);
        Long timestamp = (Long) session.getAttribute(timestampKey);
        Integer attempts = (Integer) session.getAttribute(attemptsKey);

        if (attempts == null) {
            attempts = 0;
        }

        // Check if OTP exists
        if (storedOtp == null || timestamp == null) {
            return new OTPResult(false, "No OTP found. Please request a new one.");
        }

        // Check max attempts
        if (attempts >= MAX_VERIFICATION_ATTEMPTS) {
            clearOTP(session, type);
            return new OTPResult(false, "Maximum verification attempts exceeded. Please request a new OTP.");
        }

        // Check expiry
        long otpAge = System.currentTimeMillis() - timestamp;
        if (otpAge > OTP_EXPIRY_MILLIS) {
            clearOTP(session, type);
            return new OTPResult(false, "OTP has expired. Please request a new one.");
        }

        // Increment attempts
        attempts++;
        session.setAttribute(attemptsKey, attempts);

        // Verify OTP using BCrypt (Security improvement)
        if (passwordService.checkPassword(inputOtp, storedOtp)) {
            // Success - mark as verified
            if (type == OTPType.REGISTRATION) {
                session.setAttribute("email_verified", true);
                String verifiedEmail = (String) session.getAttribute(getEmailKey(type));
                session.setAttribute("verified_email", verifiedEmail);
            } else {
                session.setAttribute("password_reset_verified", true);
            }

            // Log with masked email (Privacy improvement)
            String email = (String) session.getAttribute(getEmailKey(type));
            System.out.println("[OTP-" + type + "] Verified for: " + maskEmail(email));

            // Clear OTP data (keep verified status)
            session.removeAttribute(otpKey);
            session.removeAttribute(timestampKey);
            session.removeAttribute(attemptsKey);
            session.removeAttribute(getLastRequestKey(type));

            return new OTPResult(true, "OTP verified successfully");
        } else {
            int remainingAttempts = MAX_VERIFICATION_ATTEMPTS - attempts;
            OTPResult result = new OTPResult(false, "Invalid OTP. " + remainingAttempts + " attempts remaining.");
            result.addData("remainingAttempts", remainingAttempts);

            System.out.println("[OTP-" + type + "] Failed attempt " + attempts);
            return result;
        }
    }

    /**
     * Clear OTP data from session
     */
    public static void clearOTP(HttpSession session, OTPType type) {
        session.removeAttribute(getOTPKey(type));
        session.removeAttribute(getEmailKey(type));
        session.removeAttribute(getTimestampKey(type));
        session.removeAttribute(getAttemptsKey(type));
        session.removeAttribute(getLastRequestKey(type));
    }

    /**
     * Check if rate limit allows new request
     */
    public static boolean isRateLimitOk(HttpSession session, OTPType type) {
        String lastRequestKey = getLastRequestKey(type);
        Long lastRequestTime = (Long) session.getAttribute(lastRequestKey);

        if (lastRequestTime == null) {
            return true;
        }

        long timeSinceLastRequest = System.currentTimeMillis() - lastRequestTime;
        return timeSinceLastRequest >= RATE_LIMIT_MILLIS;
    }

    /**
     * Get remaining rate limit seconds
     */
    public static long getRateLimitRemainingSeconds(HttpSession session, OTPType type) {
        String lastRequestKey = getLastRequestKey(type);
        Long lastRequestTime = (Long) session.getAttribute(lastRequestKey);

        if (lastRequestTime == null) {
            return 0;
        }

        long timeSinceLastRequest = System.currentTimeMillis() - lastRequestTime;
        if (timeSinceLastRequest >= RATE_LIMIT_MILLIS) {
            return 0;
        }

        return (RATE_LIMIT_MILLIS - timeSinceLastRequest) / 1000;
    }

    // Helper methods to get session keys based on OTP type
    private static String getOTPKey(OTPType type) {
        return type == OTPType.REGISTRATION ? REG_OTP_KEY : FORGOT_OTP_KEY;
    }

    private static String getEmailKey(OTPType type) {
        return type == OTPType.REGISTRATION ? REG_EMAIL_KEY : FORGOT_EMAIL_KEY;
    }

    private static String getTimestampKey(OTPType type) {
        return type == OTPType.REGISTRATION ? REG_TIMESTAMP_KEY : FORGOT_TIMESTAMP_KEY;
    }

    private static String getAttemptsKey(OTPType type) {
        return type == OTPType.REGISTRATION ? REG_ATTEMPTS_KEY : FORGOT_ATTEMPTS_KEY;
    }

    private static String getLastRequestKey(OTPType type) {
        return type == OTPType.REGISTRATION ? REG_LAST_REQUEST_KEY : FORGOT_LAST_REQUEST_KEY;
    }

    /**
     * Mask email for privacy in logs
     * Example: capkimkhanh@gmail.com -> cap***@gmail.com
     */
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }

        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        // Show first 2-3 characters, mask the rest
        int visibleChars = Math.min(3, localPart.length());
        return localPart.substring(0, visibleChars) + "***" + domainPart;
    }
}
