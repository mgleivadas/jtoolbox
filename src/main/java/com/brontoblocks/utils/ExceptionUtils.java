package com.brontoblocks.utils;

import com.brontoblocks.exception.functional.ThrowingBiConsumer;
import com.brontoblocks.exception.functional.ThrowingBiFunction;
import com.brontoblocks.exception.functional.ThrowingConsumer;
import com.brontoblocks.exception.functional.ThrowingFunction;
import com.brontoblocks.exception.functional.ThrowingPredicate;
import com.brontoblocks.exception.functional.ThrowingRunnable;
import com.brontoblocks.exception.functional.ThrowingSupplier;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ExceptionUtils {

    public static void captureInRunnable(Runnable runnable) {
        captureInRunnableAndReturn(runnable);
    }

    public static Optional<Throwable> captureInRunnableAndReturn(Runnable runnable) {
        try {
            runnable.run();
            return empty();
        } catch (Throwable t) {
            return of(t);
        }
    }

    public static void captureInThrowingRunnable(ThrowingRunnable runnable) {
        captureInThrowingRunnableAndReturn(runnable);
    }

    public static Optional<Throwable> captureInThrowingRunnableAndReturn(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return empty();
        } catch (Throwable t) {
            return of(t);
        }
    }

    public static <T> Either<Throwable, T> eitherValueOrCapture(ThrowingSupplier<T> supplier) {
        try {
            return Either.right(supplier.get());
        } catch (Throwable t) {
            return Either.left(t);
        }
    }

    public static <T> T convertToIllegalStateException(ThrowingSupplier<T> s, String message) {
        try {
            return s.get();
        } catch (Throwable t) {
            throw new IllegalStateException(message, t);
        }
    }

    public static Runnable throwSilently(ThrowingRunnable t) {
        return t;
    }

    public static <T> Supplier<T> throwSilently(ThrowingSupplier<T> t) {
        return t;
    }

    public static <T> Consumer<T> throwSilently(ThrowingConsumer<T> t) {
        return t;
    }

    public static <T, U> BiConsumer<T, U> throwSilently(ThrowingBiConsumer<T, U> t) {
        return t;
    }

    public static <T, U> Function<T, U> throwSilently(ThrowingFunction<T, U> t) {
        return t;
    }

    public static <T, U, R> BiFunction<T, U, R> throwSilently(ThrowingBiFunction<T, U, R> t) {
        return t;
    }

    public static <T> Predicate<T> throwSilently(ThrowingPredicate<T> t) {
        return t;
    }
}
