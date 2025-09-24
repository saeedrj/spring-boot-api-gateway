package com.gateway.service.impl;

import com.gateway.dto.enumDto.EnumResult;
import com.gateway.dto.roleDto.AddPermissionToRoleRequestDto;
import com.gateway.dto.roleDto.CreateRoleRequestDto;
import com.gateway.dto.roleDto.RoleResponseDto;
import com.gateway.dto.roleDto.UpdateRoleRequestDto;
import com.gateway.entity.accessLevel.PermissionEntity;
import com.gateway.entity.accessLevel.RoleEntity;
import com.gateway.entity.accessLevel.RolePermissionEntity;
import com.gateway.excepotion.RecordException;
import com.gateway.repository.PermissionRepository;
import com.gateway.repository.RolePermissionRepository;
import com.gateway.repository.RoleRepository;
import com.gateway.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gateway.dto.enumDto.EnumResult.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;


    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponseDto> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::mapToDto);
    }


    @Override
    @Transactional
    public RoleResponseDto createRole(CreateRoleRequestDto request) {
        if (roleRepository.existsByNameIgnoreCase(request.name())) {
            throw new RecordException(EnumResult.ROLE_EXISTS, HttpStatus.CONFLICT);
        }
        RoleEntity role = new RoleEntity();
        role.setName(request.name());
        role.setDescription(request.description());

        RoleEntity saved = roleRepository.save(role);

        return mapToDto(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleByName(String roleName) {
        RoleEntity role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new RecordException(EnumResult.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));
        return mapToDto(role);
    }


    @Override
    @Transactional
    public RoleResponseDto updateRoleDescription(String roleName, UpdateRoleRequestDto request) {
        RoleEntity role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new RecordException(EnumResult.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));
        role.setDescription(request.description());
        return mapToDto(roleRepository.save(role));
    }


    @Override
    @Transactional
    public RoleResponseDto addPermissionToRole(String roleName, AddPermissionToRoleRequestDto request) {
        RoleEntity role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new RecordException(EnumResult.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        PermissionEntity permission = permissionRepository.findByNameIgnoreCase(request.permissionName()).stream().findFirst()
                .orElseThrow(() -> new RecordException(PERMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (role.getRolePermissions().stream().anyMatch(rp -> rp.getPermission().equals(permission))) {
            throw new RecordException(PERMISSION_ASSIGNED, HttpStatus.CONFLICT);
        }

        RolePermissionEntity rolePermission = new RolePermissionEntity();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        rolePermissionRepository.saveAndFlush(rolePermission);
        role.getRolePermissions().add(rolePermission);

        return mapToDto(role);
    }


    @Override
    @Transactional
    public RoleResponseDto removePermissionFromRole(String roleName, String permissionName) {
        RoleEntity role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new RecordException(ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        RolePermissionEntity rolePermission = role.getRolePermissions().stream()
                .filter(rp -> rp.getPermission().getName().equalsIgnoreCase(permissionName))
                .findFirst()
                .orElseThrow(() -> new RecordException(PERMISSION_NOT_ASSIGNED, HttpStatus.BAD_REQUEST));

        rolePermissionRepository.delete(rolePermission);
        role.getRolePermissions().remove(rolePermission);
        rolePermissionRepository.flush();

        return mapToDto(role);
    }


    @Override
    @Transactional
    public void deleteRole(String roleName) {
        RoleEntity role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new RecordException(ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!role.getUserRoles().isEmpty()) {
            throw new RecordException(ROLE_ASSIGNED_USER, HttpStatus.CONFLICT);
        }

        roleRepository.delete(role);
    }


    private RoleResponseDto mapToDto(RoleEntity role) {
        List<String> permissions = role.getRolePermissions().stream()
                .map(rp -> rp.getPermission().getName())
                .toList();

        return new RoleResponseDto(role.getName(), role.getDescription(), permissions);
    }
}
