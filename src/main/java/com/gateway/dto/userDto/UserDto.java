package com.gateway.dto.userDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record UserDto(String username, String email, String firstName, String lastName, LocalDateTime lastLoginDate ,
                      @Schema(description = "Role and permissions map")
                      Map<String, List<String>>  permissions) {
}
