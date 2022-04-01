package com.brontoblocks.mutable;

import java.util.Objects;

/**
 * This class is not thread-safe without external synchronization
 */
public final class VolatileMutableInt {

    public static VolatileMutableInt of(int initialValue) {
        return new VolatileMutableInt(initialValue);
    }

    private VolatileMutableInt(int value) {
        this.value = value;
    }

    public void increaseByOne() {
        ++value;
    }

    public void decreaseByOne() {
        --value;
    }

    public void increaseByN(int n) {
        value+=n;
    }

    public void decreaseByN(int n) {
        value-=n;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolatileMutableInt that = (VolatileMutableInt) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private volatile int value;
}
