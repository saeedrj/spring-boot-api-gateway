package com.gateway.dto.permissionDto;

import java.util.List;

public record RolePermissionsResponseDto(
        String roleName,
        List<PermissionResponseDto> permissions
) {}