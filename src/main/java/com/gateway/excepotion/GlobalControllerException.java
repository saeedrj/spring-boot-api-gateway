package com.gateway.excepotion;

import com.gateway.dto.ApiResponseDto;
import com.gateway.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static com.gateway.dto.enumDto.EnumResult.INTERNAL_SERVER_ERROR;
import static com.gateway.dto.enumDto.EnumResult.REQUEST_METHOD_EXCEPTION;

@ControllerAdvice
@Slf4j
public class GlobalControllerException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = error instanceof FieldError fe ? fe.getField() : "unknown";
                    return String.format("Error in field '%s': %s", field, error.getDefaultMessage());
                }).toList();

        return ResponseEntity.badRequest().body(ApiResponseDto.fail(errors));
    }


    @ExceptionHandler(RecordException.class)
    public ResponseEntity<ApiResponseDto<ResponseDto>> handleRecordException(RecordException ex) {
        return new ResponseEntity<>(ApiResponseDto.fail(ex.getException()), ex.getHttpStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<ResponseDto>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDto.fail(new ResponseDto(ex.getMessage(), 99)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<ResponseDto>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDto.fail(new ResponseDto(INTERNAL_SERVER_ERROR)));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDto<ResponseDto>> handleHttpRequestMethodNotSupportedException(Exception ex) {
        log.error("method exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponseDto.fail(new ResponseDto(REQUEST_METHOD_EXCEPTION)));
    }

}
