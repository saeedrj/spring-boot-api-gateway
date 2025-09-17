package com.gateway.dto.roleDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRoleRequestDto(
        @NotBlank(message = "Description cannot be blank")
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {}
