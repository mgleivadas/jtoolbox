package com.brontoblocks.exception;

public class SerializationException extends RuntimeException {

    public SerializationException(String message, Throwable nestedException) {
        super(message, nestedException);
    }
}
