package com.gateway.utils;

public class NotificationUtils {

    public static String getEmailMessage(String name, String host, String key) {
        return String.format(
                "Hi %s,\n\n" +
                        "Welcome! Please activate your account using the link below:\n" +
                        "%s/user/resetPassword?key=%s\n\n" +
                        "If this wasn't you, ignore this email.\n\n" +
                        "Thanks,\nYour Company Team",
                name, host, key
        );
    }


    public static String getResetPasswordMessage(String name, String resetLink) {
        return String.format(
                "Hi %s,\n\n" +
                        "We received a request to reset your password. Please click the link below to create a new password:\n\n" +
                        "%s\n\n" +
                        "This link is valid for 24 hours. If you did not request a password reset, you can safely ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Your Team",
                name,
                resetLink
        );
    }
}
