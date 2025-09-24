package com.gateway.repository;

import com.gateway.entity.accessLevel.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByName(String name);

    Optional<PermissionEntity> findByNameIgnoreCase(String name);

    boolean existsByName(String name);
}