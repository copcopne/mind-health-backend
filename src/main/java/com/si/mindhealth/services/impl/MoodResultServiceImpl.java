package com.si.mindhealth.services.impl;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.TopicMultiResult;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;
import com.si.mindhealth.entities.MoodResultTopic;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.entities.enums.SupportTopic;
import com.si.mindhealth.entities.enums.TopicType;
import com.si.mindhealth.repositories.MoodResultRepository;
import com.si.mindhealth.services.MoodResultService;
import com.si.mindhealth.services.nlp.TopicDetector;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoodResultServiceImpl implements MoodResultService {
    private final TopicDetector topicDetector;
    private final MoodResultRepository moodResultRepository;

    @Override
    @Async
    @Transactional
    public void CalculateResult(MoodEntry entry, User user) {
        // Chạy NLP
        TopicMultiResult results = topicDetector.detectMulti(entry, user.getIsAcceptSharingData());

        // ===== UP SERT THEO @MapsId (id của MoodResult == entry.getId()) =====
        Long entryId = entry.getId();
        MoodResult r = moodResultRepository.findById(entryId).orElseGet(() -> {
            // tạo mới và gắn association - KHÔNG set id thủ công
            MoodResult nr = new MoodResult();
            nr.setMoodEntry(entry); // @MapsId sẽ đồng bộ id = entryId
            return nr;
        });

        // cập nhật các trường tính toán
        r.setIsCrisis(results.isCrisis());
        r.setSentimentScore(results.sentimentScore());

        // ===== Rebuild topics an toàn để tránh duplicate =====
        if (r.getTopics() == null) {
            r.setTopics(new HashSet<>());
        } else {
            r.getTopics().clear(); // orphanRemoval=true sẽ xoá record con cũ
        }

        // Topic chính
        MoodResultTopic main = new MoodResultTopic();
        main.setTopic(SupportTopic.fromString(results.primaryTopic()));
        main.setType(TopicType.MAIN_TOPIC);
        main.setMoodResult(r);
        r.getTopics().add(main);

        // Các topic phụ (nếu có)
        for (var topic : results.otherTopics()) {
            MoodResultTopic sub = new MoodResultTopic();
            sub.setTopic(topic.topic());
            sub.setType(TopicType.SUB_TOPIC);
            sub.setMoodResult(r);
            r.getTopics().add(sub);
        }

        // Lưu (UPDATE nếu đã tồn tại, INSERT nếu lần đầu)
        moodResultRepository.save(r);
    }

    @Override
    public MoodResult getResult(MoodEntry entry) {
        Optional<MoodResult> result = moodResultRepository.findByMoodEntry(entry);
        return result.orElse(null);
    }
}
