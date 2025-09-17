package com.gateway.dto.roleDto;

import java.util.List;

public record RoleResponseDto(
        String name,
        String description,
        List<String> permissions
) {}
