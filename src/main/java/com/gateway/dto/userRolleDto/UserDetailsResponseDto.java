package com.gateway.dto.userRolleDto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDetailsResponseDto(
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDateTime lastLoginDate,
        boolean enabled,
        String manager,
        List<String> roles
) {
}