package com.si.mindhealth.services.impl;

import org.springframework.stereotype.Service;

import com.si.mindhealth.entities.ProcessingLog;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.repositories.ProcessingLogRepository;
import com.si.mindhealth.services.ProcessingLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcessingLogServiceImpl implements ProcessingLogService {
    private final ProcessingLogRepository logRepository;

    @Override
    public ProcessingLog get(TargetType targetType, Long id) {
        return logRepository
                .findTopByTargetTypeAndTargetIdOrderByCreatedAtDescIdDesc(targetType, id)
                .orElse(null);
    }

}
