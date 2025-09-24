package com.gateway.service.impl;

import com.gateway.dto.ResponseDto;
import com.gateway.dto.enumDto.EnumResult;
import com.gateway.entity.UserEntity;
import com.gateway.entity.accessLevel.PermissionEntity;
import com.gateway.entity.accessLevel.RoleEntity;
import com.gateway.entity.accessLevel.RolePermissionEntity;
import com.gateway.excepotion.RecordException;
import com.gateway.repository.PermissionRepository;
import com.gateway.repository.RolePermissionRepository;
import com.gateway.repository.RoleRepository;
import com.gateway.repository.UserRepository;
import com.gateway.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.gateway.dto.permissionDto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gateway.dto.enumDto.EnumResult.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {


    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public PermissionResponseDto createPermission(CreatePermissionRequestDto requestDto) {
        if (permissionRepository.existsByName((requestDto.name()))) {
            throw new RecordException(EnumResult.PERMISSION_EXISTS, HttpStatus.CONFLICT);
        }

        PermissionEntity permission = new PermissionEntity();
        permission.setName(requestDto.name());
        permission.setPathPermission(requestDto.pathPermission());

        PermissionEntity saved = permissionRepository.saveAndFlush(permission);
        return new PermissionResponseDto(saved.getName(), saved.getPathPermission());
    }

    @Override
    @Transactional
    public void addPermissionToRole(String roleName, String permissionName) {
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RecordException(ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        PermissionEntity permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RecordException(PERMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        boolean alreadyAssigned = role.getRolePermissions().stream()
                .anyMatch(rp -> rp.getPermission().getName().equalsIgnoreCase(permissionName));

        if (alreadyAssigned) {
            throw new RecordException(PERMISSION_ASSIGNED, HttpStatus.CONFLICT);
        }

        RolePermissionEntity rolePermission = new RolePermissionEntity();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        rolePermissionRepository.save(rolePermission);
    }


    @Override
    @Transactional
    public void removePermissionFromRole(String roleName, String permissionName) {
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RecordException(ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        RolePermissionEntity rolePermission = role.getRolePermissions().stream()
                .filter(rp -> rp.getPermission().getName().equalsIgnoreCase(permissionName))
                .findFirst()
                .orElseThrow(() -> new RecordException(PERMISSION_NOT_ASSIGNED, HttpStatus.BAD_REQUEST));

        rolePermissionRepository.delete(rolePermission);
        rolePermissionRepository.flush();
    }



    @Override
    @Transactional
    public PermissionResponseDto updatePermission(String name, UpdatePermissionRequestDto requestDto) {
        PermissionEntity permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new RecordException(PERMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        permission.setPathPermission(requestDto.pathPermission());
        PermissionEntity updated = permissionRepository.save(permission);
        permissionRepository.flush();

        return new PermissionResponseDto(updated.getName(), updated.getPathPermission());
    }

    @Override
    @Transactional
    public ResponseDto deletePermission(String name) {
        PermissionEntity permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new RecordException(PERMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        permissionRepository.delete(permission);
        permissionRepository.flush();

        return new ResponseDto(OK);
    }




    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDto getPermissionByName(String name) {
        PermissionEntity permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new RecordException(PERMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        return new PermissionResponseDto(permission.getName(), permission.getPathPermission());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(p -> new PermissionResponseDto(p.getName(), p.getPathPermission()))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDto> getPermissionsByRole(String roleName) {
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RecordException(ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        return role.getRolePermissions().stream()
                .map(rp -> new PermissionResponseDto(rp.getPermission().getName(), rp.getPermission().getPathPermission()))
                .toList();
    }




}
