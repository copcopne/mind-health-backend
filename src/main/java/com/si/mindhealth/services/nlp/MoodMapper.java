package com.si.mindhealth.services.nlp;

import com.si.mindhealth.entities.enums.MoodLevel;

public final class MoodMapper {
    private MoodMapper() {}

    /** Map từ điểm sentiment sang MoodLevel */
    public static MoodLevel fromSentiment(Sentiment.Result r) {
        double n = r.negRatio();
        // Trường hợp rỗng hoàn toàn
        if (Double.isNaN(n)) n = 0.5;

        if (n >= 0.85) return MoodLevel.NEGATIVE;
        if (n >= 0.65) return MoodLevel.SLIGHTLY_NEGATIVE;

        if (n <= 0.15) return MoodLevel.POSITIVE;
        if (n <= 0.35) return MoodLevel.SLIGHTLY_POSITIVE;

        return MoodLevel.NEUTRAL;
    }
}
