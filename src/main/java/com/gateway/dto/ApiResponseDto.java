package com.gateway.dto;


import lombok.Getter;

@Getter
public class ApiResponseDto<T> {
    private final boolean success;
    private final T response;

    private ApiResponseDto(boolean success, T response) {
        this.success = success;
        this.response = response;
    }

    public static <T> ApiResponseDto<T> ok(T data) {
        return new ApiResponseDto<>(true, data);
    }

    public static <T> ApiResponseDto<T> fail(T data) {
        return new ApiResponseDto<>(false, data);
    }

}
