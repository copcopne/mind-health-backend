package com.si.mindhealth.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Không tìm thấy người dùng");
    }
}
