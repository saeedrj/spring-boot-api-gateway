package com.gateway.dto.userRolleDto;

import com.gateway.dto.userDto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDto(
        @Schema(description = "JWT Bearer token")
        String token,
        @Schema(description = "User information")
        UserDto user) {
}
