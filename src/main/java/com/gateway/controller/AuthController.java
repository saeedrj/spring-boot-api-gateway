package com.gateway.controller;


import com.gateway.dto.ApiResponseDto;
import com.gateway.dto.userRolleDto.LoginRequestDto;
import com.gateway.dto.userRolleDto.LoginResponseDto;
import com.gateway.dto.ResponseDto;
import com.gateway.dto.userDto.ForgetPasswordRequestDto;
import com.gateway.dto.userDto.ResetPasswordRequestDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token with permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad credentials or validation error", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "423", description = "Account locked", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletRequest request) {

        LoginResponseDto loginResponse = userService.login(loginRequestDto, request);
        log.info("User {} logged in from IP {}", loginRequestDto.username(), request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponseDto.ok(loginResponse));
    }

    @Operation(
            summary = "Forgot password",
            description = "Initiate password reset process. Contact can be either a Gmail address or 11-digit phone number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset initiated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "Account locked due to too many attempts",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/forgetPassword")
    public ResponseEntity<ApiResponseDto<ResponseDto>> forgetPassword(
            @Valid @RequestBody ForgetPasswordRequestDto dto) {

        ResponseDto result = userService.forgotPassword(dto.contact());
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }


    @PostMapping("/resetPassword")
    @Operation(summary = "Reset password", description = "Reset password using valid token")
    public ResponseEntity<ApiResponseDto<ResponseDto>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto dto,
            HttpServletRequest request) {

        ResponseDto result = userService.resetPassword(dto.token(), dto.newPassword());
        log.info("User reset password from IP {}", request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }


}
