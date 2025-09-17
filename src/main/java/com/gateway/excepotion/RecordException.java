package com.gateway.excepotion;

import com.gateway.dto.ResponseDto;
import com.gateway.dto.enumDto.EnumResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class RecordException extends RuntimeException {

    private final String message;
    private final int code;
    @Getter
    private final HttpStatus httpStatus;

    public RecordException(String message, int code, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public RecordException(EnumResult result , HttpStatus httpStatus) {
        super(result.name());
        this.message = result.getMessage();
        this.code = result.getCode();
        this.httpStatus = httpStatus;
    }

    public ResponseDto getException() {
        return new ResponseDto( message , code);
    }

}
