package com.brontoblocks.valuecache;

import com.brontoblocks.utils.Either;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ConditionalValueCache<T> {

    protected ConditionalValueCache(T initialValue, Predicate<T> updateCriterion,
                                    Supplier<T> valueProducer, Consumer<Throwable> exceptionHandler) {

        this.value = initialValue;
        this.updateCriterion = updateCriterion;
        this.valueProducer = valueProducer;
        this.exceptionHandler = exceptionHandler;
        this.isCurrentlyCheckingStatus = false;
    }

    protected ConditionalValueCache(T initialValue, Predicate<T> updateCriterion,
                                    Supplier<T> valueProducer) {
        this(initialValue, updateCriterion, valueProducer, ex -> {});
    }

    protected T getSavedValue() {
        return value;
    }

    protected T checkForUpdateOrGetCurrentValue() {

        if (!isCurrentlyCheckingStatus) {
            synchronized (this) {
                if (!isCurrentlyCheckingStatus) {
                    isCurrentlyCheckingStatus = true;
                } else {
                    return value;
                }
            }

            try {

                boolean shouldUpdate = updateCriterion.test(value);

                if (shouldUpdate) {
                    T temp = getNewValue()
                            .peekIfLeft(exceptionHandler)
                            .recoverWithValue(value);

                    value = temp;
                    return temp; // Don't inline. One memory lookup less
                }

            } finally {
                isCurrentlyCheckingStatus = false;
            }
        }

        return value;
    }

    private Either<Throwable, T> getNewValue() {
        try {
            return Either.right(valueProducer.get());
        } catch (Throwable ex) {
            return Either.left(ex);
        }
    }

    private volatile T value;

    private final Supplier<T> valueProducer;
    private final Predicate<T> updateCriterion;
    private final Consumer<Throwable> exceptionHandler;

    private volatile boolean isCurrentlyCheckingStatus;
}