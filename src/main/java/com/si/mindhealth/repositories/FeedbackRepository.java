package com.si.mindhealth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Boolean existsByUserAndTargetTypeAndTargetId(User user, TargetType targetType, Long targetId);
    Optional<Feedback> findByUserAndTargetTypeAndTargetId(User user, TargetType targetType, Long targetId);
    void deleteByUser(User user);
}