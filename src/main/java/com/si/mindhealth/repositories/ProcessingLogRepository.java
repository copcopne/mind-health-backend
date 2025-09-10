package com.si.mindhealth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.si.mindhealth.entities.ProcessingLog;
import com.si.mindhealth.entities.enums.TargetType;

public interface ProcessingLogRepository extends JpaRepository<ProcessingLog, Long> {
    Optional<ProcessingLog> findTopByTargetTypeAndTargetIdOrderByCreatedAtDescIdDesc(
            TargetType targetType, Long targetId);
}
