package com.gateway.dto.permissionDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequestDto(
        @NotBlank(message = "Permission name is required")
        @Size(max = 50, message = "Permission name must be less than 50 characters")
        String name,

        @NotBlank(message = "Path permission is required")
        @Size(max = 50, message = "Path permission must be less than 50 characters")
        String pathPermission
) {}
