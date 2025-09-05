package com.si.mindhealth.repositories;

import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.repositories.projections.DailyMoodIndexView;
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

  @Query("""
      select
        function('date', me.createdAt) as day,
        avg(
          case me.moodLevel
            when com.si.mindhealth.entities.enums.MoodLevel.VERY_BAD then -2
            when com.si.mindhealth.entities.enums.MoodLevel.BAD      then -1
            when com.si.mindhealth.entities.enums.MoodLevel.NORMAL   then  0
            when com.si.mindhealth.entities.enums.MoodLevel.GOOD     then  1
            when com.si.mindhealth.entities.enums.MoodLevel.EXCELLENT then 2
          end
        ) / 2.0 as moodIndex
      from MoodEntry me
      where me.user = :user
        and (:from is null or me.createdAt >= :from)
        and (:to   is null or me.createdAt <  :to)
      group by function('date', me.createdAt)
      order by day
      """)
  List<DailyMoodIndexView> aggregateDailyMood(
      @Param("user") com.si.mindhealth.entities.User user,
      @Param("from") java.time.Instant from,
      @Param("to") java.time.Instant to);

}
