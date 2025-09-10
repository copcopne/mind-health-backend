package com.si.mindhealth.entities;

import com.si.mindhealth.entities.enums.SatisfyLevel;
import com.si.mindhealth.entities.enums.TargetType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "feedbacks", uniqueConstraints = @UniqueConstraint(name = "uk_feedback_user_target", columnNames = {
        "user_id", "target_type", "target_id" }))
@Getter
@Setter
public class Feedback extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "target_type")
    private TargetType targetType;

    @Column(nullable = false, name = "target_id")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "satisfy_level")
    private SatisfyLevel satisfyLevel;

    @Column(length = 200)
    private String content;

    @Column(nullable = false, name = "is_read")
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void setIsRead() {
        this.isRead = false;
    }

}
