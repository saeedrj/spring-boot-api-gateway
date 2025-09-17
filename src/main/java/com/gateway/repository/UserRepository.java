package com.gateway.repository;

import com.gateway.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByEmailOrPhoneNumber(String email, String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLoginDate = :lastLoginDate, u.lastIp = :lastIp WHERE u.id = :userId")
    void updateLastLoginInfo(UUID userId, LocalDateTime lastLoginDate, String lastIp);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :password , u.lastPasswordResetDate= :lastDate WHERE u.id = :userId")
    void updatePassword(UUID userId,LocalDateTime lastDate, String password);


    @Query("SELECT DISTINCT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.userRoles ur " +
            "LEFT JOIN FETCH ur.role r " +
            "LEFT JOIN FETCH r.rolePermissions rp " +
            "LEFT JOIN FETCH rp.permission p " +
            "LEFT JOIN FETCH p.serviceEntity s " +
            "WHERE u.username = :username AND u.enabled AND s.active")
    Optional<UserEntity> findEnabledUserWithPermissionsByUsername(@Param("username") String username);

}
