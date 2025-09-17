package com.gateway.controller.admin;

import com.gateway.dto.ApiResponseDto;
import com.gateway.dto.userRolleDto.*;
import com.gateway.service.UserRolleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/userRole")
@RequiredArgsConstructor
@Slf4j
public class UserRoleController {

    private final UserRolleService userRolleService;


    @Operation(summary = "Get full user details by username")
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponseDto<UserDetailsResponseDto>> getUserDetails(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponseDto.ok(userRolleService.getUserDetails(username)));
    }

    @Operation(
            summary = "Get user roles",
            description = "Retrieve a list of all roles assigned to a specific user"
    )
    @GetMapping("/{username}/roles")
    public ResponseEntity<ApiResponseDto<UserRolesResponseDto>> getUserRoles(
            @PathVariable
            @Parameter(description = "Username of the user", example = "john_doe")
                                                                                 String username) {
        return ResponseEntity.ok(ApiResponseDto.ok(userRolleService.getUserRoles(username)));
    }


    @Operation(summary = "Get permissions of a user")
    @GetMapping("/{username}/permissions")
    public ResponseEntity<ApiResponseDto<UserPermissionsResponseDto>> getUserPermissions(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponseDto.ok(userRolleService.getUserPermissions(username)));
    }

    @Operation(
            summary = "Assign role to user",
            description = "Assign a specific role to the given user"
    )
    @PostMapping("/{username}/roles")
    public ResponseEntity<ApiResponseDto<UserRolesResponseDto>> addRoleToUser(
            @Parameter(description = "Username of the user", example = "john_doe")
            @PathVariable String username,
            @RequestBody @Valid AddRoleRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponseDto.ok(userRolleService.addRoleToUser(username, requestDto)));
    }


    @Operation(
            summary = "Remove role from user",
            description = "Remove an assigned role from the given user"
    )
    @DeleteMapping("/{username}/roles/{roleName}")
    public ResponseEntity<ApiResponseDto<UserRolesResponseDto>> removeRoleFromUser(
            @Parameter(description = "Username of the user", example = "john_doe")
            @NotNull(message = "username can not be null")
            @PathVariable String username,
            @Parameter(description = "Role name to remove", example = "ADMIN")
            @NotNull(message = "roleName can not be null")
            @PathVariable String roleName) {
        return ResponseEntity.ok(ApiResponseDto.ok(userRolleService.removeRoleFromUser(username, roleName)));
    }


}
