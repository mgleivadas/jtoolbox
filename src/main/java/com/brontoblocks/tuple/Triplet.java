package com.brontoblocks.tuple;

import java.util.function.Function;

import static com.brontoblocks.tuple.TupleUtils.verifyTupleParameter;

public record Triplet<A, B, C>(A first, B second, C third) implements Tuple<A> {

    public Triplet {
        verifyTupleParameter(first);
        verifyTupleParameter(second);
        verifyTupleParameter(third);
    }

    public static <A, B, C> Triplet<A, B, C> of(A first, B second, C third) {
        return new Triplet<>(first, second, third);
    }

    public <D> Quad<A, B, C, D> toQuad(D fourth) {
        return new Quad<>(first, second, third, fourth);
    }

    @Override
    public <U> Triplet<U, B, C> withFirst(U u) {
        return new Triplet<>(u, second, third);
    }

    public <U> Triplet<A, U, C> withSecond(U u) {
        return new Triplet<>(first, u, third);
    }

    public <U> Triplet<A, B, U> withThird(U u) {
        return new Triplet<>(first, second, u);
    }

    public Triplet<B, C, A> rotate() {
        return new Triplet<>(second, third, first);
    }

    public Triplet<C, A, B> rotateTwice() {
        return new Triplet<>(third, first, second);
    }

    public <U> U map(TupleUtils.TriFunction<A, B, C, U> mapper) {
        return mapper.apply(first, second, third);
    }

    public <U> U map(Function<Triplet<A, B, C>, U> mapper) {
        return mapper.apply(this);
    }

    @Override
    public int cardinality() {
        return 3;
    }
}
