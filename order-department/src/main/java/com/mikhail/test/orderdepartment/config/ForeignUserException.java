package com.mikhail.test.orderdepartment.config;

public class ForeignUserException extends Exception {
    public ForeignUserException() {
    }

    public ForeignUserException(String message) {
        super(message);
    }

    public ForeignUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForeignUserException(Throwable cause) {
        super(cause);
    }

    public ForeignUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
