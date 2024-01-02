package com.brontoblocks.tuple;


public sealed interface Tuple<A> permits Single, Pair, Triplet, Quad {

    A first();
    <U> Tuple<U> withFirst(U u);
    int cardinality();
}
