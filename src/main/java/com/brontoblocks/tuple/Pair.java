package com.brontoblocks.tuple;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.brontoblocks.tuple.TupleUtils.verifyTupleParameter;


public record Pair<A, B>(A first, B second) implements Tuple<A> {

    public Pair {
        verifyTupleParameter(first);
        verifyTupleParameter(second);
    }

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }

    public <C> Triplet<A, B, C> toTriplet(C third) {
        return new Triplet<>(first, second, third);
    }

    public <C, D> Quad<A, B, C, D> toQuad(C third, D fourth) {
        return new Quad<>(first, second, third, fourth);
    }

    @Override
    public <U> Pair<U, B> withFirst(U u) {
        return new Pair<>(u, second);
    }

    public <U> Pair<A, U> withSecond(U u) {
        return new Pair<>(first, u);
    }

    public Pair<B, A> rotate() {
        return new Pair<>(second, first);
    }

    public <U> U map(BiFunction<A, B, U> mapper) {
        return mapper.apply(first, second);
    }

    public <U> U map(Function<Pair<A, B>, U> mapper) {
        return mapper.apply(this);
    }

    @Override
    public int cardinality() {
        return 2;
    }
}
