package com.gateway.entity.profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "user_profiles")
public class UserProfileEntity {

    @Id
    @Column(unique = true, nullable = false)
    private UUID userId;


    @Column
    private LocalDate birthDate;

    @Column(length = 500)
    private String bio;

    @Lob
    @Column
    private byte[] profilePicture;

    @Embedded
    private AddressEmbed address;

    @Column(length = 100)
    private String company;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String jobTitle;
}
