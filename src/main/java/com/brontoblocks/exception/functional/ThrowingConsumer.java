package com.brontoblocks.exception.functional;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

    @Override
    default void accept(T t) {
        try {
            acceptThrows(t);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void acceptThrows(T t) throws Exception;
}
