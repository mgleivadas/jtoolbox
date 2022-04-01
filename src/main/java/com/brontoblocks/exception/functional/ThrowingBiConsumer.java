package com.brontoblocks.exception.functional;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U> extends BiConsumer<T, U> {

    @Override
    default void accept(T t, U u) {
        try {
            acceptThrows(t, u);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    void acceptThrows(T t, U u) throws Exception;
}
