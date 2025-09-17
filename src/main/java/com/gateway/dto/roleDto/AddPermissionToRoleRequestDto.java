package com.gateway.dto.roleDto;

import jakarta.validation.constraints.NotBlank;

public record AddPermissionToRoleRequestDto(
        @NotBlank(message = "Permission name cannot be blank")
        String permissionName
) {}