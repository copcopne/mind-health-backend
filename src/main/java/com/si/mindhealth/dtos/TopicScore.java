package com.si.mindhealth.dtos;

import com.si.mindhealth.entities.enums.SupportTopic;

public record TopicScore(
    SupportTopic topic,
    int score
) {}
