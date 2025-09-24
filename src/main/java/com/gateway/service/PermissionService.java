package com.gateway.service;

import com.gateway.dto.ResponseDto;
import com.gateway.dto.permissionDto.*;

import java.util.List;

public interface PermissionService {

    PermissionResponseDto createPermission(CreatePermissionRequestDto requestDto);

    PermissionResponseDto getPermissionByName(String name);

    List<PermissionResponseDto> getAllPermissions();

    PermissionResponseDto updatePermission(String name, UpdatePermissionRequestDto requestDto);

    ResponseDto deletePermission(String name);

    List<PermissionResponseDto> getPermissionsByRole(String roleName);

    void addPermissionToRole(String roleName, String permissionName);

    void removePermissionFromRole(String roleName, String permissionName);

}
