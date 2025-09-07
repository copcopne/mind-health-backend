package com.si.mindhealth.repositories.projections;

import java.time.LocalDate;

public interface DailyMoodPointView {
    LocalDate getDay();     // alias: day      -> DATE VN
    String    getTime();    // alias: time     -> HH:mm:ss VN (để sort nếu cần)
    Integer   getValue();   // alias: value    -> -2..+2
}