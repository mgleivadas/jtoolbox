package com.brontoblocks.utils.stream.spliterators;

import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ZippingSpliterator<T, U, R> implements Spliterator<R> {

    public static <T, U, R> Spliterator<R> of(Spliterator<T> lefts, Spliterator<U> rights, BiFunction<T, U, R> combiner) {
        return new ZippingSpliterator<>(lefts, rights, combiner);
    }

    @Override
    public boolean tryAdvance(Consumer<? super R> action) {
        rightHadNext = false;
        boolean leftHadNext = leftSpliterator.tryAdvance(l ->
                rightSpliterator.tryAdvance(r -> {
                    rightHadNext = true;
                    action.accept(combiner.apply(l, r));
                }));
        return leftHadNext && rightHadNext;
    }

    @Override
    public Spliterator<R> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Math.min(leftSpliterator.estimateSize(), rightSpliterator.estimateSize());
    }

    @Override
    public int characteristics() {
        return leftSpliterator.characteristics() & rightSpliterator.characteristics()
                & ~(Spliterator.DISTINCT | Spliterator.SORTED);
    }

    private ZippingSpliterator(Spliterator<T> leftSpliterator, Spliterator<U> rightSpliterator, BiFunction<T, U, R> combiner) {
        this.leftSpliterator = leftSpliterator;
        this.rightSpliterator = rightSpliterator;
        this.combiner = combiner;
    }

    private final Spliterator<T> leftSpliterator;
    private final Spliterator<U> rightSpliterator;
    private final BiFunction<T, U, R> combiner;
    private boolean rightHadNext = false;
}
