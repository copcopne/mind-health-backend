package com.si.mindhealth.repositories;

import java.lang.classfile.TypeAnnotation.TargetType;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.si.mindhealth.entities.ProcessingLog;

public interface ProcessingLogRepository extends JpaRepository<ProcessingLog, Long> {
    Set<ProcessingLog> findByTargetTypeAndTargetId(TargetType type, Long targetId);
}
