package com.brontoblocks.mutable;

public final class VolatileMutableBoolean {

    public static VolatileMutableBoolean withFalse() {
        return VolatileMutableBoolean.of(false);
    }

    public static VolatileMutableBoolean withTrue() {
        return VolatileMutableBoolean.of(true);
    }

    public static VolatileMutableBoolean of(boolean initialValue) {
        return new VolatileMutableBoolean(initialValue);
    }

    private VolatileMutableBoolean(boolean value) {
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

    private volatile boolean value;
}
