package com.brontoblocks.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public abstract class Either<L, R> {

    public static <L, R> Either<L, R> right(R right) {
        return new Right<>(right);
    }

    public static <L, R> Either<L, R> left(L left) {
        return new Left<>(left);
    }

    public static <L, R> Either<L, R> fromElementTransformation(
            L element,
            Function<L, Optional<R>> transformationFunction) {

        var result = transformationFunction.apply(element);
        return result.<Either<L, R>>map(Either::right).orElseGet(() -> Either.left(element));
    }

    public final void consume(
            Consumer<? super L> leftConsumer,
            Consumer<? super R> rightConsumer) {

        requireNonNull(leftConsumer, "leftConsumer is null");
        requireNonNull(rightConsumer, "rightConsumer is null");

        if (hasRightSide()) {
            rightConsumer.accept(getRight());
        } else {
            leftConsumer.accept(getLeft());
        }
    }

    public final <X, Y> Either<X, Y> bimap(
            Function<? super L, ? extends X> leftMapper,
            Function<? super R, ? extends Y> rightMapper) {

        requireNonNull(leftMapper, "leftMapper is null");
        requireNonNull(rightMapper, "rightMapper is null");

        if (hasRightSide()) {
            return new Right<>(rightMapper.apply(getRight()));
        } else {
            return new Left<>(leftMapper.apply(getLeft()));
        }
    }

    public final <U> U fold(
            Function<? super L, ? extends U> leftMapper,
            Function<? super R, ? extends U> rightMapper) {

        requireNonNull(leftMapper, "leftMapper is null");
        requireNonNull(rightMapper, "rightMapper is null");
        if (hasRightSide()) {
            return rightMapper.apply(getRight());
        } else {
            return leftMapper.apply(getLeft());
        }
    }

    public final Optional<L> convertLeftSideToOptional() {
        if (hasLeftSide()) {
            return Optional.<L>of(getLeft());
        } else {
            return Optional.<L>empty();
        }
    }

    public final Optional<R> convertRightSideToOptional() {
        if (hasRightSide()) {
            return Optional.<R>of(getRight());
        } else {
            return Optional.<R>empty();
        }
    }

    public final R recoverWithValue(R recoveryValue) {
        requireNonNull(recoveryValue, "recoveryValue is null");
        if (hasLeftSide()) {
            return recoveryValue;
        } else {
            return getRight();
        }
    }

    public final R getRightOrElseMapLeft(Function<? super L, ? extends R> other) {
        requireNonNull(other, "other is null");
        if (hasRightSide()) {
            return getRight();
        } else {
            return other.apply(getLeft());
        }
    }

    public final Either<L, R> peekIfLeft(Consumer<? super L> action) {
        requireNonNull(action, "action is null");
        if (hasLeftSide()) {
            action.accept(getLeft());
        }
        return this;
    }

    public final Either<L, R> peekIfRight(Consumer<? super R> action) {
        requireNonNull(action, "action is null");
        if (hasRightSide()) {
            action.accept(getRight());
        }
        return this;
    }

    public final <X extends Throwable> R getRightOrElseThrow(Function<? super L, X> exceptionFunction) throws X {
        requireNonNull(exceptionFunction, "exceptionFunction is null");
        if (hasRightSide()) {
            return getRight();
        } else {
            throw exceptionFunction.apply(getLeft());
        }
    }

    public final Either<R, L> swap() {
        if (hasRightSide()) {
            return new Left<>(getRight());
        } else {
            return new Right<>(getLeft());
        }
    }

    public boolean isLeft() {
        return hasLeftSide();
    }

    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> flatMap(Function<? super R, ? extends Either<L, ? extends U>> mapper) {
        requireNonNull(mapper, "mapper is null");
        if (hasRightSide()) {
            return (Either<L, U>) mapper.apply(getRight());
        } else {
            return (Either<L, U>) this;
        }
    }

    public final <U> U map(Function<? super Either<L, R>, ? extends U> mapper) {
        requireNonNull(mapper, "f is null");
        return mapper.apply(this);
    }

    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> mapIfRight(Function<? super R, ? extends U> mapper) {
        requireNonNull(mapper, "mapper is null");
        if (hasRightSide()) {
            return Either.right(mapper.apply(getRight()));
        } else {
            return (Either<L, U>) this;
        }
    }

    @SuppressWarnings("unchecked")
    public final <U> Either<U, R> mapIfLeft(Function<? super L, ? extends U> leftMapper) {
        requireNonNull(leftMapper, "leftMapper is null");
        if (hasLeftSide()) {
            return Either.left(leftMapper.apply(getLeft()));
        } else {
            return (Either<U, R>) this;
        }
    }

    @SuppressWarnings("unchecked")
    public final Either<L, R> orElse(Either<? extends L, ? extends R> other) {
        requireNonNull(other, "other is null");
        return hasRightSide() ? this : (Either<L, R>) other;
    }

    @SuppressWarnings("unchecked")
    public final Either<L, R> orElse(Supplier<? extends Either<? extends L, ? extends R>> supplier) {
        requireNonNull(supplier, "supplier is null");
        return hasRightSide() ? this : (Either<L, R>) supplier.get();
    }

    public static final class Left<L, R> extends Either<L, R> {

        private final L value;

        private Left(L value) {
            this.value = value;
        }

        @Override
        public R getRight() {
            throw new NoSuchElementException("get() on Left");
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public boolean hasLeftSide() {
            return true;
        }

        @Override
        public boolean hasRightSide() {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Left && Objects.equals(value, ((Left<?, ?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return format("Left: %s", value);
        }
    }

    public static final class Right<L, R> extends Either<L, R> {

        private final R value;

        private Right(R value) {
            this.value = value;
        }

        @Override
        public R getRight() {
            return value;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("getLeft() on Right");
        }

        @Override
        public boolean hasLeftSide() {
            return false;
        }

        @Override
        public boolean hasRightSide() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Right && Objects.equals(value, ((Right<?, ?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return format("Right: %s", value);
        }
    }

    private Either() {
    }

    public abstract L getLeft();
    public abstract R getRight();

    protected abstract boolean hasLeftSide();
    protected abstract boolean hasRightSide();
}
