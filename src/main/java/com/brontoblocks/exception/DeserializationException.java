package com.brontoblocks.exception;

public class DeserializationException extends RuntimeException {

    public DeserializationException(String message, Throwable nestedException) {
        super(message, nestedException);
    }
}
