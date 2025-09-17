package com.gateway.dto;

import com.gateway.dto.enumDto.EnumResult;

public record ResponseDto(String message, int code) {

    public ResponseDto(EnumResult enumResult){
        this(enumResult.getMessage(), enumResult.getCode());
    }

    public ResponseDto(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
