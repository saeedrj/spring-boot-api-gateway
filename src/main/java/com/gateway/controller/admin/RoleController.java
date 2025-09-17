package com.gateway.controller.admin;

import com.gateway.dto.ApiResponseDto;
import com.gateway.dto.roleDto.AddPermissionToRoleRequestDto;
import com.gateway.dto.roleDto.CreateRoleRequestDto;
import com.gateway.dto.roleDto.RoleResponseDto;
import com.gateway.dto.roleDto.UpdateRoleRequestDto;
import com.gateway.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints for managing roles and their permissions")
public class RoleController {

    private final RoleService roleService;


    @Operation(
            summary = "Get all roles",
            description = "Returns paginated list of roles with their details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of roles returned")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<RoleResponseDto>>> getAllRoles(Pageable pageable) {
        Page<RoleResponseDto> result = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }


    @Operation(
            summary = "Create a new role",
            description = "Creates a new role with the given name and description",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role successfully created"),
                    @ApiResponse(responseCode = "409", description = "Role already exists"),
                    @ApiResponse(responseCode = "400", description = "Validation failed")
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> createRole(
            @Valid @RequestBody CreateRoleRequestDto request) {
        RoleResponseDto result = roleService.createRole(request);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }



    @Operation(
            summary = "Get role details by name",
            description = "Returns detailed information about a role, including its permissions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role details returned"),
                    @ApiResponse(responseCode = "404", description = "Role not found")
            }
    )
    @GetMapping("/{roleName}")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> getRoleByName(
            @Parameter(description = "Name of the role to fetch", example = "ADMIN")
            @PathVariable String roleName) {
        RoleResponseDto result = roleService.getRoleByName(roleName);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }




    @Operation(
            summary = "Update role description",
            description = "Updates the description of an existing role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Role not found")
            }
    )
    @PatchMapping("/{roleName}/description")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> updateRoleDescription(
            @PathVariable String roleName,
            @Valid @RequestBody UpdateRoleRequestDto request) {
        RoleResponseDto result = roleService.updateRoleDescription(roleName, request);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }



    @Operation(
            summary = "Add a permission to a role",
            description = "Assigns a permission to an existing role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permission added to role"),
                    @ApiResponse(responseCode = "404", description = "Role or permission not found"),
                    @ApiResponse(responseCode = "409", description = "Permission already assigned to role")
            }
    )
    @PostMapping("/{roleName}/permissions")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> addPermissionToRole(
            @PathVariable String roleName,
            @Valid @RequestBody AddPermissionToRoleRequestDto request) {
        RoleResponseDto result = roleService.addPermissionToRole(roleName, request);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }


    @Operation(
            summary = "Remove a permission from a role",
            description = "Removes a permission previously assigned to a role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Permission removed from role"),
                    @ApiResponse(responseCode = "404", description = "Role not found"),
                    @ApiResponse(responseCode = "400", description = "Permission not assigned to role")
            }
    )
    @DeleteMapping("/{roleName}/permissions/{permissionName}")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> removePermissionFromRole(
            @PathVariable String roleName,
            @PathVariable String permissionName) {
        RoleResponseDto result = roleService.removePermissionFromRole(roleName, permissionName);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }



    @Operation(
            summary = "Delete a role",
            description = "Deletes a role if it is not assigned to any user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Role not found"),
                    @ApiResponse(responseCode = "409", description = "Role is assigned to users and cannot be deleted")
            }
    )
    @DeleteMapping("/{roleName}")
    public ResponseEntity<ApiResponseDto<Void>> deleteRole(
            @PathVariable String roleName) {
        roleService.deleteRole(roleName);
        return ResponseEntity.ok(ApiResponseDto.ok(null));
    }


}
