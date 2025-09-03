package com.si.mindhealth.services.nlp;

import com.si.mindhealth.entities.enums.MoodLevel;

public final class MoodDecider {

    public static final class Decision {
        public final MoodLevel userMood;
        public final MoodLevel modelMood;
        public final MoodLevel finalMood;
        public final boolean crisis;
        public final boolean disagreed; // user vs model lệch > 1 bậc
        public final boolean overriddenByCrisis;

        public Decision(MoodLevel userMood, MoodLevel modelMood, MoodLevel finalMood,
                boolean crisis, boolean disagreed, boolean overriddenByCrisis) {
            this.userMood = userMood;
            this.modelMood = modelMood;
            this.finalMood = finalMood;
            this.crisis = crisis;
            this.disagreed = disagreed;
            this.overriddenByCrisis = overriddenByCrisis;
        }
    }

    /** Quyết định cuối cùng từ userMood + sentiment + crisis */
    public static Decision decide(MoodLevel userMoodNullable, Sentiment.Result r, boolean crisis) {
        MoodLevel model = MoodMapper.fromSentiment(r);

        // 1) Crisis override
        if (crisis) {
            MoodLevel fin = MoodLevel.VERY_BAD;
            boolean disagreed = userMoodNullable != null && distance(userMoodNullable, fin) > 1;
            return new Decision(userMoodNullable, model, fin, true, disagreed, true);
        }

        // 2) Nếu user không chọn → dùng model
        if (userMoodNullable == null) {
            return new Decision(null, model, model, false, false, false);
        }

        MoodLevel user = userMoodNullable;
        int diff = distance(user, model);

        // 3) Nếu lệch <= 1 bậc → tôn trọng user
        if (diff <= 1) {
            return new Decision(user, model, user, false, false, false);
        }

        // 4) Nếu lệch > 1 bậc → blend (70% user, 30% model), nhưng có phanh theo
        // negRatio
        int blendedVal = clamp(Math.round(0.7f * user.getValue() + 0.3f * model.getValue()));

        // “phanh” theo độ mạnh của cảm xúc để tránh quá lạc quan/bi quan:
        double n = r.negRatio();
        if (n >= 0.90)
            blendedVal = Math.min(blendedVal, MoodLevel.BAD.getValue());
        if (n <= 0.10)
            blendedVal = Math.max(blendedVal, MoodLevel.GOOD.getValue());

        MoodLevel fin = MoodLevel.fromValue(blendedVal);
        return new Decision(user, model, fin, false, true, false);
    }

    private static int distance(MoodLevel a, MoodLevel b) {
        return Math.abs(a.getValue() - b.getValue());
    }

    private static int clamp(int v) {
        return Math.max(-2, Math.min(2, v));
    }

    private MoodDecider() {
    }
}
