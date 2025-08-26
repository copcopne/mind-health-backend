package com.si.mindhealth.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.si.mindhealth.entities.Message;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.Sender;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByUser(User user, Pageable pageable);
    Optional<Message> findFirstByOrderByCreatedAtDesc();
    Optional<Message> findFirstByUserAndSenderOrderByCreatedAtDesc(User user, Sender sender);
}
