package com.si.mindhealth.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SatisfyLevel {
    VERY_BAD(-2, "Rất tệ"),
    BAD(-1, "Tệ"),
    NORMAL(0, "Bình thường"),
    GOOD(1, "Tốt"),
    EXCELLENT(2, "Xuất sắc");

    private final int value;
    private final String label;

    SatisfyLevel(int v, String label) {
        this.value = v;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static SatisfyLevel from(String input) {
        for (SatisfyLevel t : values()) {
            if (t.name().equalsIgnoreCase(input)
                    || String.valueOf(t.value).equals(input)
                    || t.label.equalsIgnoreCase(input)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid SatisfyLevel: " + input);
    }
}