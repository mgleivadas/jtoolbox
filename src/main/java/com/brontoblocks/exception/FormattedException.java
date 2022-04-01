package com.brontoblocks.exception;

public class FormattedException extends RuntimeException {
    public static FormattedException of(String formattedMessage, String... params) {
        return new FormattedException(formattedMessage, params);
    }

    protected FormattedException(String formattedException, String... params) {
        super(String.format(formattedException, (Object[]) params));
    }
}
