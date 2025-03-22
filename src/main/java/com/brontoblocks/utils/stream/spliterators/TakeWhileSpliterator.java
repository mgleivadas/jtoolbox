package com.brontoblocks.utils.stream.spliterators;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TakeWhileSpliterator<T> implements Spliterator<T> {

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        return conditionHeldSoFar && base.tryAdvance(e -> {
            if (pred.test(e)) {
                action.accept(e);
            } else {
                if (inclusive && conditionHeldSoFar) {
                    action.accept(e);
                }
                conditionHeldSoFar = false;
            }
        });
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return conditionHeldSoFar ? base.estimateSize() : 0;
    }

    @Override
    public int characteristics() {
        return base.characteristics() & ~Spliterator.SIZED;
    }

    @Override
    public Comparator<? super T> getComparator() {
        return base.getComparator();
    }

    public TakeWhileSpliterator(Spliterator<T> base, Predicate<T> pred, boolean inclusive) {
        this.base = base;
        this.pred = pred;
        this.inclusive = inclusive;
    }

    private final Spliterator<T> base;
    private final Predicate<T> pred;
    private final boolean inclusive;
    private boolean conditionHeldSoFar = true;
}
