package com.si.mindhealth.exceptions;

public class MyBadRequestException extends RuntimeException {
    public MyBadRequestException(String message) {
        super(message);
    }
}
