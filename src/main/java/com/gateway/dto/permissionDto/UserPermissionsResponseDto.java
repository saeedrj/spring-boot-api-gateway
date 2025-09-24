package com.gateway.dto.permissionDto;

import java.util.List;

public record UserPermissionsResponseDto(
        String username,
        List<String> permissions
) {}