package com.brontoblocks.exception.functional;

import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowingPredicate<T> extends Predicate<T> {

    @Override
    default boolean test(T t) {
        try {
            return testThrows(t);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    boolean testThrows(T t) throws Exception;
}
