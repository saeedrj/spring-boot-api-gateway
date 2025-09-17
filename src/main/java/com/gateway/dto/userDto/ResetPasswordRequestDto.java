package com.gateway.dto.userDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(

        @NotBlank(message = "can not send null token")
        @Schema(description = "Token received from forget password flow", example = "abc123def456")
        @Size(min = 8 , message = "your token is not valid")
        String token,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Schema(description = "New password for the user", example = "NewPassword123!")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
        String newPassword


) {
}
