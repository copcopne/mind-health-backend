package com.si.mindhealth.dtos;

import java.util.List;

public record TopicMultiResult(
    List<TopicScore> otherTopics,  // các topic qua ngưỡng, đã sort giảm dần
    String primaryTopic,          // topic chính (tên enum as String) = phần tử đầu danh sách, null nếu rỗng
    int score,
    double sentimentScore,
    boolean isCrisis,
    String modelMood, 
    String finalMood, 
    boolean disagreed, 
    boolean overriddenByCrisis
) {}
