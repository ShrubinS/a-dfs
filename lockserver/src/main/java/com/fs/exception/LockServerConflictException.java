package com.fs.exception;

public class LockServerConflictException extends RuntimeException {
    public LockServerConflictException(String message) {
        super(message);
    }

    public LockServerConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
