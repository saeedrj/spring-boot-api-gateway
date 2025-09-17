package com.gateway.repository;

import com.gateway.entity.UserSecurityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurityEntity, UUID> {


    Optional<UserSecurityEntity> findByVerificationToken(String verificationToken);

    Optional<UserSecurityEntity> findByResetPasswordToken(String resetPasswordToken);

}

