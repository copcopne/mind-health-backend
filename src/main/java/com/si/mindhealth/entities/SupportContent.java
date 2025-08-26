package com.si.mindhealth.entities;

import java.util.Set;

import com.si.mindhealth.entities.enums.SupportTopic;
import com.si.mindhealth.entities.enums.SupportType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "support_contents")
@Getter
@Setter
public class SupportContent extends BaseEntity {

    @Enumerated
    @Column(name = "support_type", nullable = false)
    private SupportType supportType;

    @Enumerated
    @Column(name = "support_topic", nullable = false)
    private SupportTopic supportTopic;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "supportContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupportResponse> supportResponses;
}
