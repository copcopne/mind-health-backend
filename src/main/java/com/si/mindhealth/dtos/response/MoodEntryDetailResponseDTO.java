package com.si.mindhealth.dtos.response;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;
import com.si.mindhealth.entities.MoodResultTopic;
import com.si.mindhealth.entities.enums.MoodLevel;
import com.si.mindhealth.entities.enums.Topic;
import com.si.mindhealth.entities.enums.TopicType;

import lombok.Data;

@Data
public class MoodEntryDetailResponseDTO {

    private Long id;

    @JsonProperty(value = "content")
    private String content;

    @JsonProperty(value = "mood_level")
    private MoodLevel moodLevel;

    @JsonProperty(value = "main_topic")
    private Topic mainTopic;

    @JsonProperty(value = "other_topics")
    private Set<Topic> otherTopics;

    @JsonProperty(value = "sentiment_score")
    private double sentimentScore;

    @JsonProperty(value = "is_crisis")
    private Boolean isCrisis;

    @JsonProperty(value = "can_edit")
    private Boolean canEdit;

    @JsonProperty(value = "can_feedback")
    private Boolean canFeedback;
    
    @JsonProperty(value = "created_at")
    private Instant createdAt;

    public MoodEntryDetailResponseDTO(MoodEntry moodEntry, boolean canEdit) {
        this(moodEntry, null, canEdit, true);
    }

    public MoodEntryDetailResponseDTO(MoodEntry moodEntry, MoodResult result, boolean canEdit, boolean canFeedback) {
        this.id = moodEntry.getId();
        this.moodLevel = moodEntry.getMoodLevel();
        this.content = moodEntry.getContent();
        this.canEdit = canEdit;
        this.canFeedback = canFeedback;
        this.createdAt = moodEntry.getCreatedAt();

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
