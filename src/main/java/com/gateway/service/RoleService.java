package com.gateway.service;

import com.gateway.dto.roleDto.AddPermissionToRoleRequestDto;
import com.gateway.dto.roleDto.CreateRoleRequestDto;
import com.gateway.dto.roleDto.RoleResponseDto;
import com.gateway.dto.roleDto.UpdateRoleRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface RoleService {

    RoleResponseDto createRole(CreateRoleRequestDto request);

    RoleResponseDto getRoleByName(String roleName);

    Page<RoleResponseDto> getAllRoles(Pageable pageable);

    RoleResponseDto updateRoleDescription(String roleName, UpdateRoleRequestDto request);

    RoleResponseDto addPermissionToRole(String roleName, AddPermissionToRoleRequestDto request);

    RoleResponseDto removePermissionFromRole(String roleName, String permissionName);

    void deleteRole(String roleName);
}