package com.brontoblocks.utils.reference;

import static com.brontoblocks.utils.ArgCheck.nonNull;

public final class Reference<T> {

  public static <T> Reference<T> create(Class<T> clazz) {
    return new Reference<>(clazz);
  }

  public static <T> Reference<T> create() {
    return new Reference<>();
  }

  public T get() {
    if (referencedValue == null) {
      throw NO_REFERENCE_SET_EXCEPTION;
    } else {
      return referencedValue;
    }
  }

  public boolean isSet() {
    return referencedValue != null;
  }

  public void capture(T value) {
    referencedValue = nonNull("referencedValue", value);
  }

  public void clear() {
    referencedValue = null;
  }

  private Reference() {
    this.referencedValue = null;
  }

  private Reference(Class<T> clazz) {
    this.referencedValue = null;
  }

  private T referencedValue;

  private static final IllegalStateException NO_REFERENCE_SET_EXCEPTION =
    new IllegalStateException("Reference has not been set");
}
