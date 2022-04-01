package com.brontoblocks.utils;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.brontoblocks.utils.ArgCheck.nonNull;
import static java.util.Optional.empty;

public abstract class Try<T> {

    public static <T> Try<T> of(Supplier<T> supplier, Runnable finallyStatement) {
        return impl(supplier, finallyStatement);
    }

    public static <T> Try<T> of(Supplier<T> supplier) {
        return impl(supplier, () -> {});
    }

    private static <T> Try<T> impl(Supplier<T> supplier, Runnable finallyStatement) {

        try {
            return new Success<>(nonNull("supplier", supplier).get());
        } catch (Throwable t) {
            return new Failure<>(t);
        } finally {
            finallyStatement.run();
        }
    }

    public abstract T complete() throws Throwable;
    public abstract T wrapAnyExceptionAndComplete();

    public abstract Try<T> onSuccess(Consumer<T> action);
    public abstract Try<T> onFailure(Consumer<Throwable> action);

    public abstract <U> Try<U> andThen(Function<? super T, ? extends U> thenable);

    public abstract T recover(Function<? super Throwable, T> recoverFunction);
    public abstract T recoverWithValue(T value);

    public abstract boolean hasSucceeded();
    public abstract boolean hasFailed();

    public abstract Either<Throwable, T> toEither();
    public abstract Optional<T> toOptional();

    public abstract Instant getTriedAt();

    public static final class Success<T> extends Try<T> {

        Success(T value) {
            this.value = value;
            this.triedAt = Instant.now();
        }

        @Override
        public T complete() {
            return value;
        }

        @Override
        public T wrapAnyExceptionAndComplete() {
            return value;
        }

        @Override
        public Try<T> onSuccess(Consumer<T> action) {
            action.accept(value);
            return this;
        }

        @Override
        public Try<T> onFailure(Consumer<Throwable> action) {
            return this;
        }

        @Override
        public <U> Try<U> andThen(Function<? super T, ? extends U> thenable) {
            return Try.of(() -> nonNull("remapping function", thenable).apply(value));
        }

        @Override
        public T recover(Function<? super Throwable, T> recoverFunction) {
            return value;
        }

        @Override
        public T recoverWithValue(T newValue) {
            return value;
        }

        @Override
        public boolean hasSucceeded() {
            return true;
        }

        @Override
        public boolean hasFailed() {
            return false;
        }

        @Override
        public Either<Throwable, T> toEither() {
            return Either.right(value);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        @Override
        public Instant getTriedAt() {
            return triedAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Success<?> success = (Success<?>) o;
            return triedAt.equals(success.triedAt) && value.equals(success.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(triedAt, value);
        }

        private final T value;
        private final Instant triedAt;

    }

    public static final class Failure<T> extends Try<T> {

        Failure(Throwable throwable) {
            this.throwable = throwable;
            this.triedAt = Instant.now();

        }

        @Override
        public T complete() throws Throwable {
            throw throwable;
        }

        @Override
        public T wrapAnyExceptionAndComplete() {
            throw new RuntimeException("Try failed.", throwable);
        }

        @Override
        public Try<T> onSuccess(Consumer<T> action) {
            return this;
        }

        @Override
        public Try<T> onFailure(Consumer<Throwable> action) {
            action.accept(throwable);
            return this;
        }

        @Override
        public <U> Try<U> andThen(Function<? super T, ? extends U> thenable) {
            return new Failure<>(throwable);
        }

        @Override
        public T recover(Function<? super Throwable, T> recoverFunction) {
            return recoverFunction.apply(throwable);
        }

        @Override
        public T recoverWithValue(T value) {
            return value;
        }

        @Override
        public boolean hasSucceeded() {
            return false;
        }

        @Override
        public boolean hasFailed() {
            return true;
        }

        @Override
        public Either<Throwable, T> toEither() {
            return Either.left(throwable);
        }

        @Override
        public Optional<T> toOptional() {
            return empty();
        }

        @Override
        public Instant getTriedAt() {
            return triedAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Failure<?> failure = (Failure<?>) o;
            return throwable.equals(failure.throwable) && triedAt.equals(failure.triedAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(throwable, triedAt);
        }

        private final Throwable throwable;
        private final Instant triedAt;
    }
}
