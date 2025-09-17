package com.gateway.service;

import com.gateway.dto.userRolleDto.LoginRequestDto;
import com.gateway.dto.userRolleDto.LoginResponseDto;
import com.gateway.dto.ResponseDto;
import com.gateway.dto.userDto.CreateAccountRequestDto;
import com.gateway.dto.userDto.CreateAccountResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface UserService {

    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest);

    CreateAccountResponseDto createAccount(CreateAccountRequestDto requestDto);

    void updateLastLoginInfo(UUID userId, String ipAddress);

    ResponseDto updatePassword(String newPassword, String currentPassword);

    ResponseDto forgotPassword(String contact);

    ResponseDto resetPassword(String token ,  String newPassword);

    ResponseDto logout( HttpServletRequest request);
}
