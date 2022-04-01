package com.brontoblocks.exception.functional;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, U> extends Function<T, U> {

    @Override
    default U apply(T t) {
        try {
            return applyThrows(t);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    U applyThrows(T t) throws Exception;
}
