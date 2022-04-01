package com.brontoblocks.exception;

public class OperationAbortedException extends RuntimeException {
    public OperationAbortedException() {
    }

    OperationAbortedException(String message, Throwable nestedException) {
        super(message, nestedException);
    }
}
