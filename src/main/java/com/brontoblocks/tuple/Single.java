package com.brontoblocks.tuple;

import java.util.function.Function;

import static com.brontoblocks.tuple.TupleUtils.verifyTupleParameter;


public record Single<A>(A first) implements Tuple<A> {

    public Single {
        verifyTupleParameter(first);
    }

    public static <A> Single<A> of(A first) {
        return new Single<>(first);
    }

    public <B> Pair<A, B> toPair(B second) {
        return new Pair<>(first, second);
    }

    public <B, C> Triplet<A, B, C> toTriplet(B second, C third) {
        return new Triplet<>(first, second, third);
    }

    public <B, C, D> Quad<A, B, C, D> toQuad(B second, C third, D fourth) {
        return new Quad<>(first, second, third, fourth);
    }

    @Override
    public <U> Single<U> withFirst(U u) {
        return new Single<>(u);
    }

    public <U> U map(Function<A, U> mapper) {
        return mapper.apply(first);
    }

    @Override
    public int cardinality() {
        return 1;
    }
}
