package com.si.mindhealth.entities;

import com.si.mindhealth.converters.MoodLevelConverter;
import com.si.mindhealth.entities.enums.MoodLevel;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mood_entries")
@Getter
@Setter
public class MoodEntry extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Convert(converter = MoodLevelConverter.class)
    @Column(nullable = false, name = "mood_level")
    private MoodLevel moodLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "moodEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private MoodResult moodResult;
}
