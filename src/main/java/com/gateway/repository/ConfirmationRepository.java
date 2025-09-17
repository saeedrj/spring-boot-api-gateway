package com.gateway.repository;

import com.gateway.entity.profile.ConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {

    Optional<ConfirmationEntity> findByKey(String key);

}
