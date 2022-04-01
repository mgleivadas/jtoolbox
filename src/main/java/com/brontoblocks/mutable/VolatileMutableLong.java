package com.brontoblocks.mutable;

import java.util.Objects;

/**
 * This class is not thread-safe without external synchronization
 */
public final class VolatileMutableLong {

    public static VolatileMutableLong of(long initialValue) {
        return new VolatileMutableLong(initialValue);
    }

    private VolatileMutableLong(long value) {
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
        VolatileMutableLong that = (VolatileMutableLong) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private volatile long value;
}
