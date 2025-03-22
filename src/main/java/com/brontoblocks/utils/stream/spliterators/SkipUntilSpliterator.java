package com.brontoblocks.utils.stream.spliterators;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SkipUntilSpliterator<T> implements Spliterator<T> {

    public SkipUntilSpliterator(Spliterator<T> source, Predicate<T> condition, boolean inclusive) {
        this.source = source;
        this.condition = condition;
        this.inclusive = inclusive;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (conditionMet) {
            return source.tryAdvance(action);
        }
        while (!conditionMet && source.tryAdvance(e -> {
            if (condition.test(e)) {
                if (!inclusive) {
                    action.accept(e);
                }
                conditionMet = true;
            }
        })) ;
        return conditionMet;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        if (!conditionMet) {
            tryAdvance(action);
        }
        if (conditionMet) {
            source.forEachRemaining(action);
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return conditionMet ? source.estimateSize() : Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return source.characteristics() & ~Spliterator.SIZED;
    }

    @Override
    public Comparator<? super T> getComparator() {
        return source.getComparator();
    }

    private final Spliterator<T> source;
    private final Predicate<T> condition;
    private final boolean inclusive;
    private boolean conditionMet = false;

}
