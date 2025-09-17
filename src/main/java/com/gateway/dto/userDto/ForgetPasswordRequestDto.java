package com.gateway.dto.userDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ForgetPasswordRequestDto(

        @NotBlank(message = "contact cannot be blank")
        @Pattern(
                regexp = "^(?:\\d{11}|[a-zA-Z0-9._%+-]+@gmail\\.com)$",
                message = "Field must be either a valid 11-digit phone number or a Gmail address"
        )
        @Schema(description = "User contact: 11-digit phone number or Gmail address", example = "09123456789")
        String contact) {
}
