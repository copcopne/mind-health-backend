package com.si.mindhealth.repositories.projections;

import java.time.LocalDate;

public interface DailyMoodIndexView {
    LocalDate getDay();     // yyyy-MM-dd
    Double getMoodIndex();  // trung bình trong ngày (cao = vui)
}
