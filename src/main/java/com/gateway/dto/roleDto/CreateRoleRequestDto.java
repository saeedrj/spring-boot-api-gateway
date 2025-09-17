package com.gateway.dto.roleDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequestDto(
        @NotBlank(message = "Role name cannot be blank")
        @Size(max = 50, message = "Role name cannot exceed 50 characters")
        String name,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {}
