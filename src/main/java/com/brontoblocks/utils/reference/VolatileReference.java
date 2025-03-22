package com.brontoblocks.utils.reference;

import static com.brontoblocks.utils.ArgCheck.nonNull;

public final class VolatileReference<T> {

  public static <T> VolatileReference<T> create(Class<T> clazz) {
    return new VolatileReference<>(clazz);
  }

  public static <T> VolatileReference<T> create() {
    return new VolatileReference<>();
  }

  public T get() {
    if (referencedValue == null) {
      throw NO_REFERENCE_SET_EXCEPTION;
    } else {
      return referencedValue;
    }
  }

  public void capture(T value) {
    referencedValue = nonNull("referencedValue", value);
  }

  private VolatileReference() {
    this.referencedValue = null;
  }

  private VolatileReference(Class<T> clazz) {
    this.referencedValue = null;
  }

  private volatile T referencedValue;

  private static final IllegalStateException NO_REFERENCE_SET_EXCEPTION =
    new IllegalStateException("Volatile reference has not been set");
}
