package com.brontoblocks.utils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is a handy utility where an expensive resource can be fetched. The fetching operation is guaranteed to
 * be performed exactly once. Multiple subsequent invocations have no effect and will be served from in memory
 * cached data.
 *
 * This method can be chained so that a new lazy loader can be generated, given a remapping function
 * (possibly containing another expensive call). The same semantics apply for the newly created LazyLoader instance.
 *
 * Furthermore, in case cached data need to be refreshed, reload method will perform once again the expensive operation.
 * If this LazyLoader was created using chain(...) method then only the remapping function will be called again and the
 * parent's cached data will not be affected. In case parent LazyLoader needs to be updated then reload should be called
 * upon that object and prior to this object's reload() invocation.
 *
 * This class IS thread safe.
 * @param <T> The expensive operation which should be cached and loaded lazily.
 */
public final class Lazyloader<T> {

    public static <T> Lazyloader<T> with(Supplier<T> operation) {
        return new Lazyloader<>(operation);
    }

    public <U> Lazyloader<U> chain(Function<T, U> remapping) {
        return Lazyloader.with(() -> remapping.apply(get()));
    }

    private Lazyloader(Supplier<T> op) {
        isReloadInProgress = new AtomicBoolean(false);
        operation = op;
        val = null;
    }

    public T reload() {
        boolean started = false;
        T localRef = null;
        try {
            if (isReloadInProgress.compareAndSet(false, true)) {
                started = true;
                val = localRef = operation.get();
                return localRef;
            } else {
                return val;
            }
        } finally {
            if (started) {
                isReloadInProgress.set(false);
            }
        }
    }

    public T get() {
        T localRef = val;
        if (localRef == null) {
            synchronized (this) {
                localRef = val;
                if (localRef == null) {
                    val = localRef = operation.get();
                }
            }
        }
        return localRef;
    }

    public void changeProvider(Supplier<T> newOperation) {
        synchronized (this) {
            if (val == null) {
                operation = newOperation;
            } else {
                throw new IllegalStateException("Provider can be configured only before any get() is called.");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Lazyloader<?> that = (Lazyloader<?>) o;
        return Objects.equals(isReloadInProgress, that.isReloadInProgress) &&
            Objects.equals(operation, that.operation) &&
            Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isReloadInProgress, operation, val);
    }

    private final AtomicBoolean isReloadInProgress;
    private volatile Supplier<T> operation;
    private volatile T val;
}
