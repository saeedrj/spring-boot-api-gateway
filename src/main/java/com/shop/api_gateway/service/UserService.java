package com.shop.api_gateway.service;

import com.shop.api_gateway.dto.LoginRequestDto;
import com.shop.api_gateway.dto.LoginResponseDto;
import com.shop.api_gateway.dto.profile.CreateAccountRequestDto;
import com.shop.api_gateway.dto.profile.CreateAccountResponseDto;

import java.util.UUID;

public interface UserService {

    LoginResponseDto login(LoginRequestDto loginRequestDto , String ip);

    CreateAccountResponseDto createAccount(CreateAccountRequestDto requestDto);

    void updateLastLoginInfo(UUID userId, String ipAddress);

    void updatePassword(UUID userId, String password);

    void updateLastPasswordResetDate(UUID userId);


}
