package com.gateway.repository;

import com.gateway.entity.accessLevel.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    ServiceEntity findByName(String name);

    ServiceEntity findByPathName(String pathName);
}
