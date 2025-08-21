package com.si.mindhealth.entities.enums;

public enum MoodLevel {

    NEGATIVE(-2, "Tiêu cực"),
    SLIGHTLY_NEGATIVE(-1, "Hơi tiêu cực"),
    NEUTRAL(0, "Trung tính"),
    SLIGHTLY_POSITIVE(1, "Hơi tích cực"),
    POSITIVE(2, "Tích cực");

    private final int value;
    private final String label;

    MoodLevel(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static MoodLevel fromValue(int value) {
        for (MoodLevel level : MoodLevel.values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy MoodLevel với giá trị: " + value);
    }
}
