package com.gateway.entity.profile;


import com.gateway.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Confirmations")
public class ConfirmationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "confirmation_key", nullable = false)
    private String key;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id" , referencedColumnName = "id")
    private UserEntity userEntity;

    public ConfirmationEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.key = UUID.randomUUID().toString();
    }
}
