package com.si.mindhealth.dtos.response;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.si.mindhealth.entities.Message;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;
import com.si.mindhealth.entities.MoodResultTopic;
import com.si.mindhealth.entities.ProcessingLog;
import com.si.mindhealth.entities.enums.SupportTopic;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.entities.enums.TopicType;

import lombok.Data;

@Data
public class OriginFeedbackObjectResponseDTO {
    private String content;

    public OriginFeedbackObjectResponseDTO(Object o, TargetType type, ProcessingLog log) {
        StringBuilder sb = new StringBuilder();

        if (o == null || type == null) {
            this.content = "(Không có dữ liệu nguồn)";
            return;
        }

        switch (type) {
            case MESSAGE -> {
                Message m = (Message) o; // Message có sender, content, createdAt
                sb.append("Tin nhắn #").append(m.getId())
                        .append(" (").append(m.getSender()).append(")")
                        .append(" — ").append(m.getCreatedAt()).append('\n')
                        .append(m.getContent());
            }
            case MOOD_ENTRY -> {
                if (o instanceof MoodResult r) {
                    MoodEntry me = r.getMoodEntry(); // MoodResult có moodEntry, isCrisis, sentimentScore, topics
                    sb.append("Nhật ký cảm xúc #")
                            .append(me != null ? me.getId() : r.getId())
                            .append(" — ").append(me != null ? me.getCreatedAt() : null).append('\n');

                    if (me != null) {
                        sb.append("Mức độ cảm xúc: ")
                                .append(me.getMoodLevel() != null ? me.getMoodLevel().name() : "N/A").append('\n') // MoodEntry
                                                                                                                   // có
                                                                                                                   // moodLevel,
                                                                                                                   // content
                                .append("Nội dung: ").append(me.getContent()).append('\n');
                    }

                    sb.append("Điểm tiêu cực: ")
                            .append(String.format("%.3f", r.getSentimentScore())).append('\n')
                            .append("Khủng hoảng: ")
                            .append(Boolean.TRUE.equals(r.getIsCrisis()) ? "Có" : "Không").append('\n'); // isCrisis,
                                                                                                         // sentimentScore

                    Set<SupportTopic> others = Collections.emptySet();
                    SupportTopic main = null;
                    if (r.getTopics() != null) {
                        main = r.getTopics().stream()
                                .filter(t -> t.getType() == TopicType.MAIN_TOPIC)
                                .map(MoodResultTopic::getTopic)
                                .findFirst().orElse(null);
                        others = r.getTopics().stream()
                                .filter(t -> t.getType() == TopicType.SUB_TOPIC)
                                .map(MoodResultTopic::getTopic)
                                .collect(Collectors.toCollection(LinkedHashSet::new));
                    }

                    if (main != null) {
                        sb.append("Chủ đề chính: ").append(main.getLabel()).append('\n'); // SupportTopic.getLabel()
                    }
                    if (!others.isEmpty()) {
                        sb.append("Chủ đề phụ: ")
                                .append(others.stream().map(SupportTopic::getLabel).collect(Collectors.joining(", ")))
                                .append('\n');
                    }
                }
            }
            default -> sb.append(o.toString());
        }

        if (log != null && log.getPayload() != null && !log.getPayload().isBlank()) {
            sb.append("\n\n[Nhật ký xử lý]\n").append(log.getPayload());
        }

        this.content = sb.toString();
    }
}
