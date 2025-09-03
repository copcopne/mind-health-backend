package com.si.mindhealth.dtos.response;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;
import com.si.mindhealth.entities.MoodResultTopic;
import com.si.mindhealth.entities.enums.MoodLevel;
import com.si.mindhealth.entities.enums.SupportTopic;
import com.si.mindhealth.entities.enums.TopicType;

import lombok.Data;

@Data
public class MoodEntryResponseDTO {

    private Long id;

    @JsonProperty(value = "short_content")
    private String shortContent;

    @JsonProperty(value = "mood_level")
    private MoodLevel moodLevel;

    @JsonProperty(value = "main_topic")
    private SupportTopic mainTopic;
    
    @JsonProperty(value = "other_topics")
    private Set<SupportTopic> otherTopics;

    @JsonProperty(value = "sentiment_score")
    private double sentimentScore;

    @JsonProperty(value = "is_crisis")
    private Boolean isCrisis;

    @JsonProperty(value = "created_at")
    private Instant createdAt;

    public MoodEntryResponseDTO(MoodEntry moodEntry) {
        this(moodEntry, null, true);
    }

    public MoodEntryResponseDTO(MoodEntry moodEntry, boolean isEditable) {
        this(moodEntry, null, isEditable);
    }

    public MoodEntryResponseDTO(MoodEntry moodEntry, MoodResult result, boolean isEditable) {
        this.id = moodEntry.getId();
        this.moodLevel = moodEntry.getMoodLevel();
        this.createdAt = moodEntry.getCreatedAt();

        String content = moodEntry.getContent();
        this.shortContent = content.length() > 100
                ? content.substring(0, 100) + "..."
                : content;

        if (result != null) {
            this.sentimentScore = result.getSentimentScore();
            this.isCrisis = result.getIsCrisis();

            Set<MoodResultTopic> topics = result.getTopics();
            this.mainTopic = topics.stream()
                    .filter(t -> t.getType() == TopicType.MAIN_TOPIC)
                    .map(MoodResultTopic::getTopic)
                    .findFirst()
                    .orElse(null);

            // Lấy topics phụ
            this.otherTopics = topics.stream()
                    .filter(t -> t.getType() == TopicType.SUB_TOPIC)
                    .map(MoodResultTopic::getTopic)
                    .collect(Collectors.toSet());
        }
    }
}
