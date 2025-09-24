package com.gateway.repository;

import com.gateway.entity.accessLevel.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String role);

    boolean existsByNameIgnoreCase(String name);

    Optional<RoleEntity> findByNameIgnoreCase(String name);
}
