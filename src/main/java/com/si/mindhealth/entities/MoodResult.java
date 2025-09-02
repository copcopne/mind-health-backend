package com.si.mindhealth.entities;

import java.util.Set;

import com.si.mindhealth.entities.enums.SupportTopic;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mood_results")
@Getter
@Setter
public class MoodResult extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_entry_id", nullable = false, unique = true)
    private MoodEntry moodEntry;

    @Column(name = "is_crisis", nullable = false)
    private Boolean isCrisis;

    @Column(name = "sentiment_score", nullable = false)
    private double sentimentScore;


    @OneToMany(mappedBy = "moodResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MoodResultTopic> topics;
    
}
