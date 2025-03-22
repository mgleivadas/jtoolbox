package com.brontoblocks.exception;

public final class WrappedCheckedException extends RuntimeException {

  public static WrappedCheckedException of(Throwable cause) {
    return new WrappedCheckedException(cause);
  }

  private WrappedCheckedException(Throwable cause) {
    super(cause);
  }

}
