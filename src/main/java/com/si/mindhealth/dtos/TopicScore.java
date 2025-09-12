package com.si.mindhealth.dtos;

import com.si.mindhealth.entities.enums.Topic;

public record TopicScore(
    Topic topic,
    int score
) {}
