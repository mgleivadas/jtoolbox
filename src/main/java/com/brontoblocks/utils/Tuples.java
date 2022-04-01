package com.brontoblocks.utils;

import java.util.Objects;
import java.util.function.Function;

import static com.brontoblocks.utils.ArgCheck.nonNull;
import static java.lang.String.format;

public final class Tuples {

    public static <T, Y> T2<T, Y> t2(T t, Y y) {
        return new T2<>(t,y);
    }

    public static <T, Y, Z> T3<T, Y, Z> t3(T t, Y y, Z z) {
        return new T3<>(t, y, z);
    }

    public static <T, Y, Z, I> T4<T, Y, Z, I> t4(T t, Y y, Z z, I i) {
        return new T4<>(t, y, z, i);
    }

    public static class T2<T, Y> {

        protected T2(T first, Y second) {
            this.first = nonNull("first", first);
            this.second = nonNull("second", second);
        }

        private final T first;
        private final Y second;

        public T getFirst() {
            return first;
        }

        public Y getSecond() {
            return second;
        }

        public <U> T2<T, U> remapRightValue(Function<Y, U> remapper) {
            return t2(this.getFirst(), nonNull("remapper function", remapper.apply(getSecond())));
        }

        public <U> T2<U, Y> remapLeftValue(Function<T, U> remapper) {
            return t2(nonNull("remapper function", remapper.apply(getFirst())), getSecond());
        }

        public <U> T2<T, U> withNewRightValue(U u) {
            return t2(getFirst(), nonNull("right value", u));
        }

        public <U> T2<U, Y> withNewLeftValue(U u) {
            return t2(nonNull("left value", u), this.getSecond());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            T2<?, ?> t2 = (T2<?, ?>) o;
            return Objects.equals(first, t2.first) &&
                    Objects.equals(second, t2.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return format("{ %s %s }", first, second);
        }
    }

    public static class T3<T, Y, Z> {

        protected T3(T first, Y second, Z third) {
            this.first = nonNull("first", first);
            this.second = nonNull("second", second);
            this.third = nonNull("third", third);
        }

        private final T first;
        private final Y second;
        private final Z third;

        public T getFirst() {
            return first;
        }

        public Y getSecond() {
            return second;
        }

        public Z getThird() {
            return third;
        }

        public <U> T3<U, Y, Z> remapAValue(Function<T, U> remapper) {
            return t3(nonNull("remapper function", remapper.apply(getFirst())), getSecond(), getThird());
        }

        public <U> T3<T, U, Z> remapBValue(Function<Y, U> remapper) {
            return t3(getFirst(), nonNull("remapper function", remapper.apply(getSecond())), getThird());
        }

        public <U> T3<T, Y, U> remapCValue(Function<Z, U> remapper) {
            return t3(getFirst(), getSecond(), nonNull("remapper function", remapper.apply(getThird())));
        }

        public <U> T3<U, Y, Z> withNewAValue(U u) {
            return t3(nonNull("first", u), getSecond(), getThird());
        }

        public <U> T3<T, U, Z> withNewBValue(U u) {
            return t3(getFirst(), nonNull("second", u), getThird());
        }

        public <U> T3<T, Y, U> withNewCValue(U u) {
            return t3(getFirst(), getSecond(), nonNull("third", u));
        }

        public T2<T, Y> keepAB() {
            return t2(first, second);
        }

        public T2<T, Z> keepAC() {
            return t2(first, third);
        }

        public T2<Y, Z> keepBC() {
            return t2(second, third);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final T3<?, ?, ?> t3 = (T3<?, ?, ?>) o;
            return Objects.equals(first, t3.first) &&
                       Objects.equals(second, t3.second) &&
                       Objects.equals(third, t3.third);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second, third);
        }

        @Override
        public String toString() {
            return format("{ %s %s %s }", first, second, third);
        }
    }

    public static class T4<T, Y, Z, I> {

        protected T4(T first, Y second, Z third, I fourth) {
            this.first = nonNull("first", first);
            this.second = nonNull("second", second);
            this.third = nonNull("third", third);
            this.fourth = nonNull("fourth", fourth);
        }

        private final T first;
        private final Y second;
        private final Z third;
        private final I fourth;

        public T getFirst() {
            return first;
        }

        public Y getSecond() {
            return second;
        }

        public Z getThird() {
            return third;
        }

        public I getFourth() {
            return fourth;
        }

        public <U> T4<U, Y, Z, I> remapAValue(Function<T, U> remapper) {
            return t4(nonNull("remapper function", remapper.apply(getFirst())), getSecond(), getThird(), getFourth());
        }

        public <U> T4<T, U, Z, I> remapBValue(Function<Y, U> remapper) {
            return t4(getFirst(), nonNull("remapper function", remapper.apply(getSecond())), getThird(), getFourth());
        }

        public <U> T4<T, Y, U, I> remapCValue(Function<Z, U> remapper) {
            return t4(getFirst(), getSecond(), nonNull("remapper function", remapper.apply(getThird())), getFourth());
        }

        public <U> T4<T, Y, Z, U> remapDValue(Function<I, U> remapper) {
            return t4(getFirst(), getSecond(), getThird(), nonNull("remapper function", remapper.apply(getFourth())));
        }

        public <U> T4<U, Y, Z, I> withNewAValue(U u) {
            return t4(nonNull("first", u), getSecond(), getThird(), getFourth());
        }

        public <U> T4<T, U, Z, I> withNewBValue(U u) {
            return t4(getFirst(), nonNull("second", u), getThird(), getFourth());
        }

        public <U> T4<T, Y, U, I> withNewCValue(U u) {
            return t4(getFirst(), getSecond(), nonNull("third", u), getFourth());
        }

        public <U> T4<T, Y, Z, U> withNewDValue(U u) {
            return t4(getFirst(), getSecond(), getThird(), nonNull("fourth", u));
        }

        public T3<T, Y, Z> keepABC() {
            return t3(first, second, third);
        }

        public T3<T, Z, I> keepABD() {
            return t3(first, third, fourth);
        }

        public T3<Y, Z, I> keepBCD() {
            return t3(second, third, fourth);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final T4<?, ?, ?, ?> t4 = (T4<?, ?, ?, ?>) o;
            return Objects.equals(first, t4.first) &&
                       Objects.equals(second, t4.second) &&
                       Objects.equals(third, t4.third) &&
                       Objects.equals(fourth, t4.fourth);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second, third, fourth);
        }

        @Override
        public String toString() {
            return format("{ %s %s %s %s }", first, second, third, fourth);
        }
    }
}
