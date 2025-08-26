package com.si.mindhealth.exceptions;

public class AuthException extends RuntimeException {
    public AuthException() {
        super("Xảy ra lỗi khi thực hiện đăng nhập vào hệ thống!");
    }

    public AuthException(String message) {
        super(message);
    }
}
