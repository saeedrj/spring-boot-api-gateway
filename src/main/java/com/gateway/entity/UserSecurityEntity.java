package com.gateway.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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
