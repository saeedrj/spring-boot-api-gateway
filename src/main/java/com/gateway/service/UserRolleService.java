package com.gateway.service;

import com.gateway.dto.ResponseDto;
import com.gateway.dto.userRolleDto.*;

public interface UserRolleService {

    UserDetailsResponseDto getUserDetails(String username);

    UserRolesResponseDto getUserRoles(String username);

    UserPermissionsResponseDto getUserPermissions(String username);

    UserRolesResponseDto addRoleToUser(String username, AddRoleRequestDto requestDto);

    UserRolesResponseDto removeRoleFromUser(String username, String roleName);

}
