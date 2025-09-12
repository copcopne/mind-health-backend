package com.si.mindhealth.entities.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Topic {

    GENERAL("Chung"),

    LIFE("Cuộc sống"),
    SOCIAL("Giao tiếp xã hội"),
    MYSELF("Bản thân"),
    STUDY("Học tập"),
    LOVE("Tình cảm"),

    FAMILY("Gia đình"),
    MENTAL_HEALTH("Sức khoẻ tinh thần"),
    WORK("Công việc"),
    HEALTH("Sức khoẻ thể chất"),
    MONEY("Tài chính"),

    TRAUMA("Tổn thương tâm lý"),
    LONELINESS("Cô đơn"),
    MOTIVATION("Động lực"),
    DECISION("Phân vân"),
    FUTURE("Tương lai");

    private final String label;

    Topic(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    private static final Map<String, Topic> LOOKUP = new HashMap<>();

    static {
        for (Topic t : values()) {
            // enum name (upper-case)
            LOOKUP.put(t.name().toUpperCase(), t);
            // nhãn vi (exact)
            LOOKUP.put(t.getLabel(), t);
        }
    }

    public static Topic fromString(String s) {
        if (s == null)
            return GENERAL;
        Topic t = LOOKUP.get(s.toUpperCase());
        if (t == null) {
            t = LOOKUP.get(s);
        }
        return t != null ? t : GENERAL;
    }

}
