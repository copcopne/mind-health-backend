package com.si.mindhealth.services;

import com.si.mindhealth.entities.ProcessingLog;
import com.si.mindhealth.entities.enums.TargetType;

public interface ProcessingLogService {
    ProcessingLog get(TargetType targetType, Long id);
}
