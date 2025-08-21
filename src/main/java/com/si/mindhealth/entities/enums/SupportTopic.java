package com.si.mindhealth.entities.enums;

public enum SupportTopic {

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

    SupportTopic(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

