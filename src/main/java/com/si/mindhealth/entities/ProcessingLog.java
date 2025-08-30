package com.si.mindhealth.entities;

import com.si.mindhealth.entities.enums.TargetType;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "processing_logs")
@Getter
@Setter
public class ProcessingLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(columnDefinition = "TEXT")
    private String payload;
}
