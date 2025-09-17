package com.gateway.dto.eventDto;

import com.gateway.dto.enumDto.EventType;
import com.gateway.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEventDto {
    private UserEntity user;
    private EventType type;
    private Map<?,?> data;
}

