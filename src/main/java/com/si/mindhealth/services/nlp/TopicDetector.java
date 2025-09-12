package com.si.mindhealth.services.nlp;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.si.mindhealth.dtos.TopicMultiResult;
import com.si.mindhealth.dtos.TopicScore;
import com.si.mindhealth.entities.MoodEntry;
import com.si.mindhealth.entities.ProcessingLog;
import com.si.mindhealth.entities.enums.MoodLevel;
import com.si.mindhealth.entities.enums.Topic;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.repositories.ProcessingLogRepository;
import com.si.mindhealth.services.TopicKeywordStore;
import com.si.mindhealth.utils.Fuzzy;
import com.si.mindhealth.utils.NormalizeInput;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicDetector {

    private final TopicKeywordStore store;
    private final ProcessingLogRepository processingLogRepository;
    private final ObjectMapper objectMapper;

    // cấu hình
    private static final int BIGRAM_BONUS = 2;
    private static final int UNIGRAM_SCORE = 1;
    private static final int MIN_SCORE_TO_ASSIGN = 2;
    private static final int MAX_TOPICS = 4;

    private static final Set<String> STOPWORDS = Set.of(
            "la", "thi", "minh", "hom", "nay", "khong", "rat", "hoi", "va", "muon", "du", "di", "choi", "tai",
            "khi", "cua", "cho", "lam", "vi");

    // 100k, 2.5k, 50 k
    private static final Pattern MONEY_K = Pattern.compile(
            "\\b\\d+(?:[.,]\\d+)?\\s?k\\b",
            Pattern.CASE_INSENSITIVE);

    // Số + (ngan|nghin): 3 ngan, 10 nghin, 2.5 ngan...
    private static final Pattern MONEY_NGAN_NUMBER = Pattern.compile(
            "\\b\\d+(?:[.,]\\d+)?\\s*(?:ngan|nghin)\\b",
            Pattern.CASE_INSENSITIVE);

    // (chuc|tram) + (ngan|nghin): chuc ngan, tram nghin, vai chuc ngan (vẫn match
    // phần "chuc ngan")
    private static final Pattern MONEY_NGAN_WORD = Pattern.compile(
            "\\b(?:chuc|tram)\\s+(?:ngan|nghin)\\b",
            Pattern.CASE_INSENSITIVE);

    // 10.000 / 100,000 / 1.000.000 (ngưỡng nghìn trở lên có nhóm 3 số)
    private static final Pattern MONEY_THOUSANDS_GROUPED = Pattern.compile(
            "\\b\\d{1,3}(?:[.,]\\d{3})+\\b");

    @Transactional
    public TopicMultiResult detectMulti(MoodEntry moodEntry, Boolean saveLog, Boolean isCrisis) {
        String rawNote = moodEntry.getContent();
        MoodLevel userMood = moodEntry.getMoodLevel();
        // 1) Chuẩn hoá
        String text = NormalizeInput.normalizeForMatch(rawNote);

        // 2) KHÔNG lọc stopwords -> cho phrase
        List<String> tokensAll = Arrays.stream(text.split("\\s+"))
                .filter(t -> !t.isBlank())
                .toList();

        // 3) CÓ lọc stopwords -> cho unigram
        List<String> tokens = tokensAll.stream()
                .filter(t -> !STOPWORDS.contains(t))
                .toList();

        Set<String> tokenSet = new HashSet<>(tokens);

        // 4) Bigram từ tokensAll (giữ “lam”)
        List<String> bigramsAll = new ArrayList<>();
        for (int i = 0; i < tokensAll.size() - 1; i++) {
            bigramsAll.add(tokensAll.get(i) + " " + tokensAll.get(i + 1));
        }

        // 5) Text đã lọc để check boundary unigram
        String filtered = String.join(" ", tokens);
        String filteredPadded = " " + filtered + " ";

        // 6) Tính điểm cho TẤT CẢ topic
        Map<Topic, Integer> scores = new EnumMap<>(Topic.class);

        for (var entry : store.all().entrySet()) {
            Topic topic = entry.getKey();
            int score = 0;

            // Lọc các keywords lặp trong json
            Set<String> uniqKeywords = new LinkedHashSet<>();
            for (String kw : entry.getValue()) {
                String k = NormalizeInput.normalizeForMatch(kw);
                if (!k.isBlank())
                    uniqKeywords.add(k);
            }

            for (String kw : uniqKeywords) {
                String k = NormalizeInput.normalizeForMatch(kw);
                boolean isPhrase = k.contains(" ");

                if (isPhrase) {
                    boolean hit = bigramsAll.contains(k);
                    if (hit) {
                        score += BIGRAM_BONUS;
                    } else {
                        // fuzzy phrase trên cửa sổ 2- từ của tokensAll
                        for (int i = 0; i < tokensAll.size() - 1; i++) {
                            String win = tokensAll.get(i) + " " + tokensAll.get(i + 1);
                            if (Fuzzy.close(win, k)) {
                                score += BIGRAM_BONUS;
                                break;
                            }
                        }
                    }
                } else {
                    // unigram check bằng tokens + filteredPadded
                    if (tokenSet.contains(k) || filteredPadded.contains(" " + k + " ")) {
                        score += UNIGRAM_SCORE;
                    } else if (k.length() >= 4) {
                        for (String t : tokens) {
                            if (t.length() >= 4 && Fuzzy.close(t, k)) {
                                score += UNIGRAM_SCORE;
                                break;
                            }
                        }
                    }
                }
            }

            // tie-break rules
            if (topic == Topic.WORK &&
                    (filteredPadded.contains(" sep ") || filteredPadded.contains(" kpi ")
                            || filteredPadded.contains(" hop ") || filteredPadded.contains(" ot "))) {
                score += 1;
            }
            if (topic == Topic.FAMILY &&
                    (filteredPadded.contains(" gia dinh ") || filteredPadded.contains(" ap luc gia dinh "))) {
                score += 1;
            }
            if (topic == Topic.WORK &&
                    (filteredPadded.contains(" cong viec ") || tokenSet.contains("deadline"))) {
                score += 1;
            }
            if (topic == Topic.MENTAL_HEALTH &&
                    (filteredPadded.contains(" ap luc ") || filteredPadded.contains(" lo au ")
                            || filteredPadded.contains(" kiet suc "))) {
                score += 1;
            }
            if (topic == Topic.LONELINESS && filteredPadded.contains(" co don ")) {
                score += 1;
            }

            if (topic == Topic.MONEY &&
                    (filteredPadded.contains(" li xi ")
                            || filteredPadded.contains(" mung tuoi ")
                            || (filteredPadded.contains(" thuong tet ")
                                    || filteredPadded.contains(" tien thuong "))
                            || MONEY_K.matcher(filteredPadded).find()
                            || MONEY_NGAN_NUMBER.matcher(filteredPadded).find()
                            || MONEY_NGAN_WORD.matcher(filteredPadded).find()
                            || MONEY_THOUSANDS_GROUPED.matcher(filteredPadded).find())) {
                score += 1;
            }

            scores.put(topic, score);
        }

        // === CRISIS BOOST: ép MENTAL_HEALTH nếu có tín hiệu khủng hoảng ===
        if (isCrisis != null && isCrisis) {
            scores.merge(Topic.MENTAL_HEALTH, 5, Integer::sum);
        } else if (isCrisis == null) {
            isCrisis = CrisisDetector.hasCrisisSignal(text);
        }

        // 7) Lọc những topic qua ngưỡng & sort
        // đổi chỗ build passed một chút
        List<TopicScore> passed = scores.entrySet().stream()
                .filter(e -> e.getValue() >= MIN_SCORE_TO_ASSIGN)
                .map(e -> new TopicScore(e.getKey(), e.getValue())) // topic = enum
                .sorted((a, b) -> Integer.compare(b.score(), a.score()))
                .toList();

        // 8) Giới hạn số topic lại
        List<TopicScore> hits = passed;
        if (MAX_TOPICS > 0 && passed.size() > MAX_TOPICS) {
            hits = passed.subList(0, MAX_TOPICS);
        }

        // 9) Chia topic chính và phụ
        String primaryTopic = hits.isEmpty() ? Topic.GENERAL.getLabel() : hits.get(0).topic().getLabel();
        List<TopicScore> otherTopics = hits.stream().skip(1).toList();
        int primaryScore = hits.isEmpty() ? 0 : hits.get(0).score();

        // 10) Tính điểm cảm xúc
        Sentiment.Result r = Sentiment.analyze(text);
        double negRatio = r.negRatio();

        // 11) Mood blend (user + model) + crisis override
        MoodLevel model = MoodMapper.fromSentiment(r);
        MoodDecider.Decision md = MoodDecider.decide(userMood, r, isCrisis);

        // 12) build payload log
        if (saveLog == true) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("moodEntryId", moodEntry.getId());
            payload.put("rawNote", rawNote);
            payload.put("normalizedText", text);
            payload.put("userMood", userMood != null ? userMood.name() : null);
            payload.put("scores", scores.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue)));
            payload.put("passed", passed.stream()
                    .map(ts -> Map.of(
                            "topicLabel", ts.topic(),
                            "score", ts.score()))
                    .toList());

            payload.put("hits", hits.stream()
                    .map(ts -> Map.of(
                            "topicLabel", ts.topic(),
                            "score", ts.score()))
                    .toList());
            payload.put("primaryTopic", primaryTopic);
            payload.put("primaryScore", primaryScore);
            payload.put("negRatio", negRatio);
            payload.put("crisis", isCrisis);
            payload.put("modelMood", model.name());
            payload.put("finalMood", md.finalMood.name());
            payload.put("disagreed", md.disagreed);
            payload.put("overriddenByCrisis", md.overriddenByCrisis);

            String jsonPayload;
            try {
                jsonPayload = objectMapper.writeValueAsString(payload);
                log.info("NLP Processing Log: {}", jsonPayload);
            } catch (Exception e) {
                log.error("Failed to serialize processing log", e);
                jsonPayload = "{}"; // fallback
            }

            // 13) Lưu log
            ProcessingLog logEntity = new ProcessingLog();
            logEntity.setTargetType(TargetType.MOOD_ENTRY);
            logEntity.setTargetId(moodEntry.getId());
            logEntity.setPayload(jsonPayload);

            processingLogRepository.save(logEntity);
        }

        // 14) Build DTO
        return new TopicMultiResult(
                otherTopics, // các topic qua ngưỡng (đã limit & sort)
                primaryTopic, // có thể null nếu không có hit nào
                primaryScore, // int: 0 nếu không có hit
                negRatio, // sentimentScore: đang dùng negRatio [0..1]
                isCrisis, // isCrisis
                model.name(), // modelMood (string)
                md.finalMood.name(), // finalMood (string)
                md.disagreed, // disagreed
                md.overriddenByCrisis // overriddenByCrisis
        );
    }
}
