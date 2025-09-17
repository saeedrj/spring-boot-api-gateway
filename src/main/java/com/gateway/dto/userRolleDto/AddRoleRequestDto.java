package com.gateway.dto.userRolleDto;

import jakarta.validation.constraints.NotBlank;

public record AddRoleRequestDto(
        @NotBlank(message = "roleName cannot be blank")
        String roleName
) {
}
