package com.si.mindhealth.entities;

import com.si.mindhealth.entities.enums.SupportTopic;
import com.si.mindhealth.entities.enums.TopicType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mood_result_topics")
@Getter
@Setter
public class MoodResultTopic extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportTopic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_result_id", nullable = false)
    private MoodResult moodResult;
}
