package com.si.mindhealth.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.TargetType;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Boolean existsByUserAndTargetTypeAndTargetId(User user, TargetType targetType, Long targetId);

    Optional<Feedback> findByUserAndTargetTypeAndTargetId(User user, TargetType targetType, Long targetId);

    void deleteByUser(User user);

    @Query("""
                select f
                from Feedback f
                join f.user u
                where (lower(f.content) like lower(concat('%', :kw, '%')) )
                  and (:type is null or f.targetType = :type)
                order by f.isRead asc, f.createdAt desc
            """)
    Page<Feedback> search(
            @Param("kw") String kw,
            @Param("type") TargetType type,
            Pageable pageable);

}