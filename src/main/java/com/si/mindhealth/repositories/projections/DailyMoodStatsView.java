package com.si.mindhealth.repositories.projections;

import java.time.LocalDate;
import com.si.mindhealth.entities.enums.MoodLevel;

public interface DailyMoodStatsView {
    LocalDate getDay();
    MoodLevel getMoodLevel();
    long getCount();
    Double getAvgSentiment();
}
