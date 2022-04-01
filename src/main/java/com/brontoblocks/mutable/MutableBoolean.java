package com.brontoblocks.mutable;

import java.util.Objects;

public final class
MutableBoolean {

    public static MutableBoolean withFalse() {
        return MutableBoolean.of(false);
    }

    public static MutableBoolean withTrue() {
        return MutableBoolean.of(true);
    }

    public static MutableBoolean of(boolean initialValue) {
        return new MutableBoolean(initialValue);
    }

    private MutableBoolean(boolean value) {
        this.value = value;
    }

    public void setFalse() {
        setValue(false);
    }

    public void setTrue() {
        setValue(true);
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isFalse() {
        return !getValue();
    }

    public boolean isTrue() {
        return getValue();
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableBoolean that = (MutableBoolean) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private boolean value;
}
