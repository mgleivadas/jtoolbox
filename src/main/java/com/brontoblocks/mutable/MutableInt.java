package com.brontoblocks.mutable;

import java.util.Objects;

public final class MutableInt {

    public static MutableInt of(int initialValue) {
        return new MutableInt(initialValue);
    }

    private MutableInt(int value) {
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
        MutableInt that = (MutableInt) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private int value;
}
