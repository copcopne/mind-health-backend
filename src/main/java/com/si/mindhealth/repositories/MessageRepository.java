package com.si.mindhealth.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.si.mindhealth.entities.Message;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.Sender;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByUser(User user, Pageable pageable);
    Optional<Message> findFirstByUserOrderByCreatedAtDesc(User user);
    Optional<Message> findFirstByUserAndSenderOrderByCreatedAtDesc(User user, Sender sender);
    List<Message> findTop4ByUserAndSenderOrderByCreatedAtDesc(User user, Sender sender);
    boolean existsByUserAndCreatedAtAfter(User user, Instant createdAt);
    boolean existsByIdAndUserAndSender(Long id, User user, Sender sender);
}
