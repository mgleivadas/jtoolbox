package com.brontoblocks.tuple;

import java.util.function.Function;

import static com.brontoblocks.tuple.TupleUtils.verifyTupleParameter;

public record Quad<A, B, C, D>(A first, B second, C third, D fourth) implements Tuple<A> {

    public Quad {
        verifyTupleParameter(first);
        verifyTupleParameter(second);
        verifyTupleParameter(third);
        verifyTupleParameter(fourth);
    }

    public static <A, B, C, D> Quad<A, B, C, D> of(A first, B second, C third, D fourth) {
        return new Quad<>(first, second, third, fourth);
    }

    @Override
    public <U> Quad<U, B, C, D> withFirst(U u) {
        return new Quad<>(u, second, third, fourth);
    }

    public <U> Quad<A, U, C, D> withSecond(U u) {
        return new Quad<>(first, u, third, fourth);
    }

    public <U> Quad<A, B, U, D> withThird(U u) {
        return new Quad<>(first, second, u, fourth);
    }

    public <U> Quad<A, B, C, U> withFourth(U u) {
        return new Quad<>(first, second, third, u);
    }

    public Quad<B, C, D, A> rotate() {
        return new Quad<>(second, third, fourth, first);
    }

    public Quad<C, D, A, B> rotateTwice() {
        return new Quad<>(third, fourth, first, second);
    }

    public Quad<D, A, B, C> rotateThrice() {
        return new Quad<>(fourth, first, second, third);
    }

    public <U> U map(TupleUtils.QuadFunction<A, B, C, D, U> mapper) {
        return mapper.apply(first, second, third, fourth);
    }

    public <U> U map(Function<Quad<A, B, C, D>, U> mapper) {
        return mapper.apply(this);
    }

    @Override
    public int cardinality() {
        return 4;
    }
}
