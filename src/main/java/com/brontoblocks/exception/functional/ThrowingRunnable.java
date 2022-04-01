package com.brontoblocks.exception.functional;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {

    @Override
    default void run() {
        try {
            runThrows();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void runThrows() throws Exception;
}
