package com.si.mindhealth.exceptions;

public class AuthException extends RuntimeException {
    public AuthException() {
        super("Tài khoản hoặc mật khẩu không đúng!");
    }

    public AuthException(String message) {
        super(message);
    }
}
