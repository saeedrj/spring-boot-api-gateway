package com.gateway.controller.admin;

import com.gateway.dto.ApiResponseDto;
import com.gateway.dto.ResponseDto;
import com.gateway.dto.permissionDto.*;
import com.gateway.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Manage permissions and role/user assignments")
public class PermissionController {

    private final PermissionService permissionService;


    @Operation(summary = "Get all permissions")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<PermissionResponseDto>>> getAllPermissions() {
        List<PermissionResponseDto> result = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }


    @Operation(summary = "Create a new permission")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponseDto<PermissionResponseDto>> createPermission(
            @Valid @RequestBody CreatePermissionRequestDto requestDto) {
        PermissionResponseDto result = permissionService.createPermission(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.ok(result));
    }


    @Operation(summary = "Update permission by name")
    @PatchMapping("/{name}")
    public ResponseEntity<ApiResponseDto<PermissionResponseDto>> updatePermission(
            @Parameter(description = "Permission name") @PathVariable String name,
            @Valid @RequestBody UpdatePermissionRequestDto requestDto) {
        PermissionResponseDto result = permissionService.updatePermission(name, requestDto);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }

    @Operation(summary = "Delete a permission by name")
    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponseDto<ResponseDto>> deletePermission(
            @Parameter(description = "Permission name") @PathVariable String name) {
        ResponseDto result = permissionService.deletePermission(name);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }



    @Operation(summary = "Get all permissions for a role")
    @GetMapping("/roles/{roleName}")
    public ResponseEntity<ApiResponseDto<List<PermissionResponseDto>>> getRolePermissions(
            @Parameter(description = "Role name") @PathVariable String roleName) {
        List<PermissionResponseDto> result = permissionService.getPermissionsByRole(roleName);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }


}
