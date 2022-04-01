package com.brontoblocks.utils;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class MicroType<T> {

    protected MicroType(T t) {
        val = requireNonNull(t);
    }

    public T getVal() {
        return val;
    }

    protected final T val;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MicroType<?> microType = (MicroType<?>) o;
        return Objects.equals(val, microType.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }
}
