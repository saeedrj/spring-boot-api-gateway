package com.gateway.excepotion;

public class LogicException extends RuntimeException {
    public LogicException(String message) {
        super(message);
    }

    public LogicException() {
        super("on error occurred");
    }


}