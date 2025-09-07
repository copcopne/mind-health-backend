package com.si.mindhealth.repositories;

import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.repositories.projections.DailyMoodPointView;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
  Optional<MoodEntry> findByIdAndUser(Long id, User user);

  Optional<MoodEntry> findByIdAndUser_Username(Long id, String username);

  Optional<MoodEntry> findTopByUserOrderByCreatedAtDesc(User user);

  Page<MoodEntry> findByUser(User user, Pageable pageable);

  Boolean existsByIdAndUser(Long id, User user);

  void deleteByUser(User user);

  @Query("""
        select
          function('date', function('convert_tz', me.createdAt, '+00:00', '+07:00')) as day,
          function('time_format', function('convert_tz', me.createdAt, '+00:00', '+07:00'), '%H:%i:%s') as time,
          (case me.moodLevel
            when com.si.mindhealth.entities.enums.MoodLevel.VERY_BAD  then -2
            when com.si.mindhealth.entities.enums.MoodLevel.BAD       then -1
            when com.si.mindhealth.entities.enums.MoodLevel.NORMAL    then  0
            when com.si.mindhealth.entities.enums.MoodLevel.GOOD      then  1
            when com.si.mindhealth.entities.enums.MoodLevel.EXCELLENT then  2
          end) as value
        from MoodEntry me
        where me.user = :user
          and (:from is null or me.createdAt >= :from)
          and (:to   is null or me.createdAt <  :to)
        order by day, time
      """)
  List<DailyMoodPointView> listDailyMoodPoints(@Param("user") User user,
      @Param("from") java.time.Instant from,
      @Param("to") java.time.Instant to);
}
