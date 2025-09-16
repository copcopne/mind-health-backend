package com.si.mindhealth.services;

import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;
import com.si.mindhealth.entities.User;

public interface MoodResultService {
    MoodResult getResult(MoodEntry entry);

    MoodResult get(Long id);

    void CalculateResult(MoodEntry entry, User user);

    void CalculateResult(MoodEntry entry, User user, Boolean isCrisis);
    
    void CalculateResult(MoodEntry entry, User user, Boolean isCrisis, String normed);
}
