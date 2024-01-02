package com.brontoblocks.tuple;

class TupleUtils {

    public static <A> void verifyTupleParameter(A param) {
        if (param == null) {
            throw new NullArgumentException();
        }
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, U> {
        U apply(A a, B b, C c);
    }

    @FunctionalInterface
    public interface QuadFunction<A, B, C, D, U> {
        U apply(A a, B b, C c, D d);
    }
}
