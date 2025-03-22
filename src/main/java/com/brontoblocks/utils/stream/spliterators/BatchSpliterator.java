package com.brontoblocks.utils.stream.spliterators;

import com.brontoblocks.mutable.MutableLong;
import com.brontoblocks.utils.stream.Batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class BatchSpliterator<E> implements Spliterator<Batch<E>> {

    public BatchSpliterator(Spliterator<E> base, int batchSize) {
        this.base = base;
        this.batchSize = batchSize;
    }

    private final int batchSize;
    private final Spliterator<E> base;
    private final MutableLong batchCounter = MutableLong.of(1);

    @Override
    public boolean tryAdvance(Consumer<? super Batch<E>> action) {
        final List<E> batch = new ArrayList<>(batchSize);

        var i = 0;
        while (i++ < batchSize) {
            var elementAdded = base.tryAdvance(batch::add);
            if (!elementAdded) {
                break;
            }
        }

        if (batch.isEmpty()) {
            return false;
        } else {
            action.accept(new Batch<>(batchCounter.getValue(), batch));
            batchCounter.increaseByOne();
            return true;
        }
    }

    @Override
    public Spliterator<Batch<E>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return -1L;
    }

    @Override
    public int characteristics() {
        return base.characteristics();
    }
}
