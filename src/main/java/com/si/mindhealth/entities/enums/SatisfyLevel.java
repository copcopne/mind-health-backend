package com.si.mindhealth.entities.enums;

public enum SatisfyLevel {
    VERY_BAD(1), BAD(2), NORMAL(3), GOOD(4), EXCELLENT(5);

    private final int value;
    
    SatisfyLevel(int v) { this.value = v; }
    public int getValue() { return value; }
}