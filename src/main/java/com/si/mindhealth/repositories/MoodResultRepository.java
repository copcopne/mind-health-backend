package com.si.mindhealth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;

public interface MoodResultRepository extends JpaRepository<MoodResult, Long> {
    Optional<MoodResult> findByMoodEntry(MoodEntry entry);
}