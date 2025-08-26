package com.si.mindhealth.entities;

import java.util.Set;

import com.si.mindhealth.entities.enums.MoodLevel;
import com.si.mindhealth.entities.enums.SupportTopic;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mood_entries")
@Getter
@Setter
public class MoodEntry extends BaseEntity {

    @Enumerated
    @Column(nullable = false, name = "mood_level")
    private MoodLevel moodLevel;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "detected_topic")
    private SupportTopic detectedTopic;

    @Column(name = "is_risky", nullable = false)
    private Boolean isRisky;

    @OneToMany(mappedBy = "moodEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupportResponse> supportResponses;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PostPersist
    private void setRisky() {
        this.isRisky = false;
    }

}
