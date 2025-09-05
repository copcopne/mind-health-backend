package com.si.mindhealth.repositories;

import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    Optional<MoodEntry> findByIdAndUser(Long id, User user);
    Optional<MoodEntry> findByIdAndUser_Username(Long id, String username);
    Optional<MoodEntry> findTopByUserOrderByCreatedAtDesc(User user);
    Page<MoodEntry> findByUser(User user, Pageable pageable);
    Boolean existsByIdAndUser(Long id, User user);
}
