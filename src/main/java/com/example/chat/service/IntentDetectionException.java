package com.example.chat.service;

public class IntentDetectionException extends RuntimeException {
    public IntentDetectionException(String message) {
        super(message);
    }

    public IntentDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
} 