package com.gateway.dto.userDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequestDto(

        @NotBlank(message = "currentPassword cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
        @Schema(description = "Current password of the user", example = "OldPassword123!")
        String currentPassword,

        @NotBlank(message = "newPassword cannot be blank")
        @Schema(description = "New password for the user", example = "NewPassword321!")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
        String newPassword) {}