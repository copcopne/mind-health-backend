package com.si.mindhealth.exceptions;

public class InvalidOldPasswordException extends RuntimeException {
    public InvalidOldPasswordException() {
        super("Mật khẩu cũ không đúng!");
    }
}
