package com.gateway.entity;


import com.gateway.entity.accessLevel.UserRoleEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 10, unique = true)
    private String phoneNumber;

    @Column
    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private boolean enabled;

    @Version
    private Long version;

    @Column
    private String lastIp;

    @Column
    private LocalDateTime lastPasswordResetDate;

    @Column
    private LocalDateTime CreatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private UserEntity manager;


    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private Set<UserEntity> subordinates = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRoleEntity> userRoles = new HashSet<>();

}
