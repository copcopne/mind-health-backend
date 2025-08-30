package com.si.mindhealth.services.impl;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.TopicMultiResult;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.MoodResult;
import com.si.mindhealth.entities.MoodResultTopic;
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
    public void CalculateResult(MoodEntry entry) {
        TopicMultiResult resuls = topicDetector.detectMulti(entry);

        MoodResult r = new MoodResult();
        r.setMoodEntry(entry);
        r.setTopics(new HashSet<>());
        r.setIsCrisis(resuls.isCrisis());
        r.setSentimentScore(resuls.sentimentScore());

        // Them topic chinh
        MoodResultTopic t = new MoodResultTopic();
        t.setTopic(resuls.primaryTopic());
        t.setType(TopicType.MAIN_TOPIC);
        t.setMoodResult(r);
        r.getTopics().add(t);
         
        // Them cac topic phu neu co
        for(var topic: resuls.otherTopics()) { 
            MoodResultTopic mrt = new MoodResultTopic();
            mrt.setTopic(topic.topic());
            mrt.setType(TopicType.SUB_TOPIC);
            mrt.setMoodResult(r);
            r.getTopics().add(mrt);
        }
        
        moodResultRepository.save(r);
    }

    @Override
    public MoodResult getResult(MoodEntry entry) {
        Optional<MoodResult> result = moodResultRepository.findByMoodEntry(entry);
        if (result.isEmpty())
            return null;
        return result.get();
    }
}
