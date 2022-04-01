package com.brontoblocks.mutable;

import java.util.Objects;

public final class MutableLong {

    public static MutableLong of(long initialValue) {
        return new MutableLong(initialValue);
    }

    private MutableLong(long value) {
        this.value = value;
    }

    public void increaseByOne() {
        ++value;
    }

    public void decreaseByOne() {
        --value;
    }

    public void increaseByN(long n) {
        value+=n;
    }

    public void decreaseByN(long n) {
        value-=n;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableLong that = (MutableLong) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private long value;
}
