package com.si.mindhealth.dtos.response;

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
public class MoodEntryDetailResponseDTO {

    private Long id;

    @JsonProperty(value = "content")
    private String content;

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

    @JsonProperty(value = "is_editable")
    private Boolean isEditable;

    public MoodEntryDetailResponseDTO(MoodEntry moodEntry, boolean isEditable) {
        this(moodEntry, null, isEditable);
    }

    public MoodEntryDetailResponseDTO(MoodEntry moodEntry, MoodResult result, boolean isEditable) {
        this.id = moodEntry.getId();
        this.moodLevel = moodEntry.getMoodLevel();
        this.content = moodEntry.getContent();
        this.isEditable = isEditable;

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
