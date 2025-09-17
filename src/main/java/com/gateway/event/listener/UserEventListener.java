package com.gateway.event.listener;

import com.gateway.dto.eventDto.UserEventDto;
import com.gateway.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void onUserEvent(UserEventDto event) {
        switch (event.getType()) {
            case REGISTRATION ->
                    notificationService.sendNewAccountNotification(event.getUser().getFirstName(), event.getUser().getEmail(), (String) event.getData().get("key"));
            case REST_PASSWORD ->
                    notificationService.sendPasswordResetNotification(event.getUser().getFirstName(), event.getUser().getEmail(), (String) event.getData().get("key"));
            default -> {
            }
        }
    }
}