package com.brontoblocks.exception.functional;

import static com.brontoblocks.utils.ArgCheck.nonNull;

@FunctionalInterface
public interface TriConsumer<T, U, V> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     */
    void accept(T t, U u, V v);

    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {

        return (l, r, v) -> {
            accept(l, r, v);
            nonNull("consumer", after).accept(l, r, v);
        };
    }
}
