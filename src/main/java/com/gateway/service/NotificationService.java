package com.gateway.service;

public interface NotificationService {

    void sendNewAccountNotification(String name, String contact, String token);

    void sendPasswordResetNotification(String name, String contact, String token);
}
