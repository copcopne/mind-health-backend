package com.si.mindhealth.entities.enums;

public enum SupportType {
    QUOTE("Trích dẫn"),
    ENCOURAGE("Lời động viên"),
    MINDFULNESS("Ý thức"),
    REMINDER("Nhắc nhở");

    private final String label;

    SupportType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
