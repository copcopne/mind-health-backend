package com.si.mindhealth.services;

import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;

public interface MoodResultService {
    MoodResult getResult(MoodEntry entry);
    void CalculateResult(MoodEntry entry);
}
