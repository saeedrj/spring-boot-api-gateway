package com.gateway.service.impl;

import com.gateway.excepotion.LogicException;
import com.gateway.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.gateway.utils.NotificationUtils.getEmailMessage;
import static com.gateway.utils.NotificationUtils.getResetPasswordMessage;


@Service
@RequiredArgsConstructor
@Slf4j
public class emailServiceImpl implements NotificationService {

    private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private static final String REST_PASSWORD_REQUEST = "Rest Password Request";

    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordResetNotification(String name, String contact, String token) {
        try {
            log.info("Attempting to send sendPasswordRestEmail email to: {}", contact);

            var message = new SimpleMailMessage();
            message.setSubject(REST_PASSWORD_REQUEST);
            message.setFrom(fromEmail);
            message.setTo(contact);
            message.setText(getResetPasswordMessage(name, host+"/"+ token));
            sender.send(message);
        } catch (Exception e) {
            log.error("Failed to send sendPasswordRestEmail email to: {}. Error: {}", contact, e.getMessage(), e);
            throw new LogicException("unable to send Email");
        }

    }

    @Override
    @Async
    public void sendNewAccountNotification(String name, String contact, String token) {
        try {
            log.info("Attempting to send new account verification email to: {}", contact);

            var message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(contact);
            message.setText(getEmailMessage(name, host, token));
            sender.send(message);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}. Error: {}", contact, e.getMessage(), e);
            throw new LogicException("unable to send Email");
        }
    }

}
