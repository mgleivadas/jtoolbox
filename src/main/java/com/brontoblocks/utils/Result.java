package com.brontoblocks.utils;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Optional.empty;

public abstract class Result {

    public static Result of(Runnable runnable, Runnable finallyStatement) {
        return impl(runnable, finallyStatement);
    }

    public static Result of(Runnable runnable) {
        return impl(runnable, () -> {});
    }

    private static Result impl(Runnable runnable, Runnable finallyStatement) {

        try {
            runnable.run();
            return new Success();
        } catch (Throwable t) {
            return new Failure(t);
        } finally {
            finallyStatement.run();
        }
    }
    public abstract Result onSuccess(Runnable action);
    public abstract Result onFailure(Consumer<Throwable> action);

    public abstract Result andThen(Consumer<Optional<Throwable>> thenable);
    public abstract <U> U andThen(Function<Optional<Throwable>, U> thenable);

    public abstract boolean hasSucceeded();
    public abstract boolean hasFailed();

    public abstract Optional<Throwable> toOptional();
    public abstract Instant getTriedAt();

    public static final class Success extends Result {

        Success() {
            this.triedAt = Instant.now();
        }

        @Override
        public Result onSuccess(Runnable action) {
            action.run();
            return this;
        }

        @Override
        public Result onFailure(Consumer<Throwable> action) {
            return this;
        }

        @Override
        public Result andThen(Consumer<Optional<Throwable>> thenable) {
            thenable.accept(empty());
            return this;
        }

        @Override
        public <U> U andThen(Function<Optional<Throwable>, U> thenable) {
            return thenable.apply(empty());
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
        public Optional<Throwable> toOptional() {
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
            Success success = (Success) o;
            return triedAt.equals(success.triedAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(triedAt);
        }

        private final Instant triedAt;
    }

    public static final class Failure extends Result {

        Failure(Throwable throwable) {
            this.throwable = throwable;
            this.triedAt = Instant.now();
        }

        @Override
        public Result onSuccess(Runnable action) {
            return this;
        }

        @Override
        public Result onFailure(Consumer<Throwable> action) {
            action.accept(throwable);
            return this;
        }

        @Override
        public Result andThen(Consumer<Optional<Throwable>> thenable) {
            thenable.accept(Optional.of(throwable));
            return this;
        }

        @Override
        public <U> U andThen(Function<Optional<Throwable>, U> thenable) {
            return thenable.apply(Optional.of(throwable));
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
        public Optional<Throwable> toOptional() {
            return Optional.of(throwable);
        }

        @Override
        public Instant getTriedAt() {
            return triedAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Failure failure = (Failure) o;
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
