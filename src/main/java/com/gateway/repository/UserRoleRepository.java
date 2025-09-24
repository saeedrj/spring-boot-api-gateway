package com.gateway.repository;

import com.gateway.entity.UserEntity;
import com.gateway.entity.accessLevel.RoleEntity;
import com.gateway.entity.accessLevel.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

    long deleteByUser(UserEntity user);

    void removeUserRoleEntitiesByRole(RoleEntity role);
}
