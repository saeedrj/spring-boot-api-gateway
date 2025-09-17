package com.gateway.dto.userRolleDto;

import java.util.List;

public record UserRolesResponseDto(
        String username,
        List<String> roles
) {
}
