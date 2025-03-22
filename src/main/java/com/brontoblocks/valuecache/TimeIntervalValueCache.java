package com.brontoblocks.valuecache;

import com.brontoblocks.utils.Tuples.T2;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.brontoblocks.utils.Tuples.t2;

public class TimeIntervalValueCache<T> extends ConditionalValueCache<T2<T, Instant>> {

    public static <T> TimeIntervalValueCache<T> create(T initialValue, long minMillisInterval,
                                                       Supplier<T> valueProducer, Consumer<Throwable> exceptionHandler) {
        return new TimeIntervalValueCache<>(
                t2(initialValue, Instant.now()),
                getPredicateIntervalInSeconds(minMillisInterval),
                () -> t2(valueProducer.get(), Instant.now()), exceptionHandler);
    }

    private TimeIntervalValueCache(T2<T, Instant> initialValue, Predicate<T2<T, Instant>> updateCriterion,
                                   Supplier<T2<T, Instant>> valueProducer, Consumer<Throwable> exceptionHandler) {
        super(initialValue, updateCriterion, valueProducer, exceptionHandler);
    }

    public T getValue() {
        return this.checkForUpdateOrGetCurrentValue().getFirst();
    }

    private static <T> Predicate<T2<T, Instant>> getPredicateIntervalInSeconds(long millis) {
        return info -> {
            long nowTime = Instant.now().toEpochMilli();
            long expirationTime = info.getSecond().plusMillis(millis).toEpochMilli();
            return nowTime - expirationTime > 0L;
        };
    }
}