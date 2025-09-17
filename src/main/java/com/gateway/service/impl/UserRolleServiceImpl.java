package com.gateway.service.impl;


import com.gateway.dto.ResponseDto;
import com.gateway.dto.userRolleDto.*;
import com.gateway.entity.UserEntity;
import com.gateway.entity.permission.RoleEntity;
import com.gateway.entity.permission.UserRoleEntity;
import com.gateway.excepotion.RecordException;
import com.gateway.repository.RoleRepository;
import com.gateway.repository.UserRepository;
import com.gateway.repository.UserRoleRepository;
import com.gateway.service.UserRolleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gateway.dto.enumDto.EnumResult.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRolleServiceImpl implements UserRolleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;


    @Transactional(readOnly = true)
    @Override
    public UserDetailsResponseDto getUserDetails(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        String manager = user.getManager() != null ? user.getManager().getUsername() : null;

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        return new UserDetailsResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getLastLoginDate(),
                user.isEnabled(),
                manager,
                roles
        );
    }

    @Transactional(readOnly = true)
    @Override
    public UserRolesResponseDto getUserRoles(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        return new UserRolesResponseDto(user.getUsername(), roles);
    }

    @Transactional(readOnly = true)
    @Override
    public UserPermissionsResponseDto getUserPermissions(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Map<String, List<String>> rolePermissions = new LinkedHashMap<>();

        for (UserRoleEntity ur : user.getUserRoles()) {
            RoleEntity role = ur.getRole();
            List<String> permissions = role.getRolePermissions().stream()
                    .map(rp -> rp.getPermission().getPathPermission())
                    .toList();
            rolePermissions.put(role.getName(), permissions);
        }

        List<String> effectivePermissions = rolePermissions.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        return new UserPermissionsResponseDto(user.getUsername(), rolePermissions, effectivePermissions);
    }


    @Override
    @Transactional
    public UserRolesResponseDto addRoleToUser(String username, AddRoleRequestDto requestDto) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        RoleEntity role = roleRepository.findByName(requestDto.roleName())
                .orElseThrow(() -> new RecordException(ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        boolean alreadyHasRole = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase(role.getName()));

        if (!alreadyHasRole) {
            UserRoleEntity userRole = new UserRoleEntity();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
            user.getUserRoles().add(userRole);
        }

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        return new UserRolesResponseDto(user.getUsername(), roles);
    }


    @Override
    @Transactional
    public UserRolesResponseDto removeRoleFromUser(String username, String roleName) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        UserRoleEntity userRole = user.getUserRoles().stream()
                .filter(ur -> ur.getRole().getName().equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new RecordException(ROLE_NOT_ASSIGNED, HttpStatus.BAD_REQUEST));

        user.getUserRoles().remove(userRole);
        userRoleRepository.delete(userRole);
        userRoleRepository.flush();

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .toList();

        return new UserRolesResponseDto(user.getUsername(), roles);
    }


}
