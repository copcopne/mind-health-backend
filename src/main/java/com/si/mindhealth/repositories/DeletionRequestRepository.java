package com.si.mindhealth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.si.mindhealth.entities.DeletionRequest;
import com.si.mindhealth.entities.User;

public interface DeletionRequestRepository extends JpaRepository<DeletionRequest, Long> {
    List<DeletionRequest> findByProcessedAtIsNull();
    Optional<DeletionRequest> findByUser(User user);
    boolean existsByUser(User user);
}
