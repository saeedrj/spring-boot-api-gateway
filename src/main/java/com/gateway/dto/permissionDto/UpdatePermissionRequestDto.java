package com.gateway.dto.permissionDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePermissionRequestDto(
        @NotBlank(message = "Path permission is required")
        @Size(max = 50, message = "Path permission must be less than 50 characters")
        String pathPermission
) {}
