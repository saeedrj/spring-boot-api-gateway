package com.gateway.controller;

import com.gateway.dto.ApiResponseDto;
import com.gateway.dto.ResponseDto;
import com.gateway.dto.userDto.CreateAccountRequestDto;
import com.gateway.dto.userDto.CreateAccountResponseDto;
import com.gateway.dto.userDto.UpdatePasswordRequestDto;
import com.gateway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Sign up user",
            description = "Create a new user account. Requires an authenticated manager user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = CreateAccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Manager user not found",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Email/Username/Phone already used",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/signUp")
    public ResponseEntity<ApiResponseDto<CreateAccountResponseDto>> signUp(
            @Valid @RequestBody CreateAccountRequestDto requestDto) {

        CreateAccountResponseDto responseDto = userService.createAccount(requestDto);
        return ResponseEntity.ok(ApiResponseDto.ok(responseDto));
    }


    @Operation(
            summary = "Update user password",
            description = "Allows an authenticated user to update their password. New password must be different from current password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, password mismatch or invalid input",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/updatePassword")
    public ResponseEntity<ApiResponseDto<ResponseDto>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequestDto dto,
            HttpServletRequest request) {

        ResponseDto result = userService.updatePassword(dto.newPassword(), dto.currentPassword());
        log.info("User updated password from IP {}", request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }


    @Operation(
            summary = "Logout the authenticated user",
            description = "Invalidates the current user's JWT token and removes it from the server cache (Redis). " +
                    "This endpoint requires the Authorization header with a valid Bearer token.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successful",
                            content = @Content(schema = @Schema(implementation = ApiResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or missing token",
                            content = @Content(schema = @Schema(implementation = ApiResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiResponseDto.class))
                    )
            }
    )
    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponseDto<ResponseDto>> logout(HttpServletRequest request) {
        ResponseDto result = userService.logout(request);
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }

}
