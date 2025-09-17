package com.gateway.dto.enumDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumResult {

    //login
    IP_LOCK("Account or Ip Locked", 101),
    AUTHENTICATION_FAILED("Authentication failed", 101),
    BAD_CREDENTIALS("Bad credentials", 400),
    ACCOUNT_DISABLED("Account disabled", 103),

    //createAccount
    EMAIL_IS_USED("Email already in use", 101),
    USER_IS_USED("Username already in use", 101),
    PHONE_IS_USED("PhoneNumber already in use", 101),
    ERROR_CREATE_ACCOUNT("Error creating account", 500),

    USER_NOT_FOUND("User Not Found", 100),
    ROLE_NOT_FOUND("Role Not Found", 101),

    ROLE_NOT_ASSIGNED("Role not assigned to user", 102),
    ROLE_ASSIGNED_USER("Role is assigned to users and cannot be deleted", 102),

    ROLE_EXISTS("Role already exists", 103),

    //PERMISSION Service
    PERMISSION_NOT_FOUND("Permission not found", 101),
    PERMISSION_ASSIGNED("Permission already assigned to role", 107),
    PERMISSION_NOT_ASSIGNED("Permission not assigned to role", 108),

    // updatePassword
    PASSWORD_IS_NOT_MACH("password is not mach", 102),
    PASSWORD_CHANGED("Password changed successfully", 200),
    BAD_REQUEST("Bad Request", 400),

    //resetPassword
    TOKEN_NOT_FOUNT("Token not found", 401),
    CHANGED_PASSWORD("your password is changed", 202),

    //forgotPassword
    LOCK_ACCOUNT("Lock Account", 406),

    //logout
    NOT_VALID_TOKEN("Token is not valid", 405),

    FALLBACK_SERVICE("Fallback Service , Service is disabled", 104),

    FORBIDDEN("Forbidden", 403),
    UNAUTHORIZED("Unauthorized", 405),

    OK("OK", 200),

    INTERNAL_SERVER_ERROR("Internal Server Error", 500),

    ;


    private final String message;
    private final int code;
}
