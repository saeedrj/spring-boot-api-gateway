package com.gateway.dto.userRolleDto;

import java.util.List;
import java.util.Map;

public record UserPermissionsResponseDto(
        String username,
        Map<String, List<String>> rolePermissions,
        List<String> effectivePermissions
) {
}
