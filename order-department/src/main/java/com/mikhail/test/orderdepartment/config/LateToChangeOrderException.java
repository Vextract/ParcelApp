package com.mikhail.test.orderdepartment.config;

public class LateToChangeOrderException extends Exception {

    public LateToChangeOrderException() {
    }

    public LateToChangeOrderException(String message) {
        super(message);
    }

    public LateToChangeOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public LateToChangeOrderException(Throwable cause) {
        super(cause);
    }

    public LateToChangeOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
