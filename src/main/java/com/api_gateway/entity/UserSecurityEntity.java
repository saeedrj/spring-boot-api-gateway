package com.api_gateway.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_security")
public class UserSecurityEntity implements Serializable {

    @Id
    @Column(unique = true, nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String salt;

    @Column
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiryDate;

    @Column
    private String resetPasswordToken;

    @Column
    private LocalDateTime resetPasswordTokenExpiryDate;

    @Column
    private LocalDateTime accountLockoutExpiryDate;

    @Column
    private boolean twoFactorEnabled;

    @Column
    private String twoFactorSecret;

    @Column
    private LocalDateTime lastPasswordFailureDate;

    @Column
    private boolean accountLocked;

    @Column(length = 500)
    private String accountLockedReason;
}
