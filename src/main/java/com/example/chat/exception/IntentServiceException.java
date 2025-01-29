package com.example.chat.exception;

public class IntentServiceException extends RuntimeException {
    public IntentServiceException(String failedToExtractTopIntent, Exception e) {
        super(failedToExtractTopIntent, e);     
    }

}
