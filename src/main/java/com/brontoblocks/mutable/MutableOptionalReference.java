package com.brontoblocks.mutable;

import java.util.Objects;
import java.util.Optional;

import static com.brontoblocks.utils.ArgCheck.nonNull;

public final class MutableOptionalReference<T> {

    public static <T> MutableOptionalReference<T> empty() {
        return new MutableOptionalReference<>(Optional.empty());
    }

    public static <T> MutableOptionalReference<T> of(T initialValue) {
        return new MutableOptionalReference<>(Optional.of(nonNull("initialValue", initialValue)));
    }

    public T getReferenceOrThrow() {
        return t.orElseThrow();
    }

    public Optional<T> getReference() {
        return t;
    }

    public void setValue(T value) {
        this.t = Optional.of(nonNull("value", value));
    }

    public void clear() {
        this.t = Optional.empty();
    }

    private MutableOptionalReference(Optional<T> t) {
        this.t = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableOptionalReference<?> that = (MutableOptionalReference<?>) o;
        return t.equals(that.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t);
    }

    private Optional<T> t;
}
