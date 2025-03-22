package com.brontoblocks.utils;

import com.brontoblocks.exception.WrappedCheckedException;
import com.brontoblocks.exception.functional.ThrowingRunnable;
import com.brontoblocks.exception.functional.ThrowingSupplier;
import com.brontoblocks.functional.QuadFunction;
import com.brontoblocks.functional.QuintFunction;
import com.brontoblocks.functional.TriFunction;

import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.brontoblocks.utils.ArgCheck.nonNull;
import static java.util.Optional.empty;

/**
 * This class is a monadic container that encapsulates computations which may result in an exception.
 * Consequently, any error handling is being approached in a functional and expressive manner, minimizing the
 * boilerplate code typically associated with try-catch-finally blocks, thus improving code readability and
 * maintainability.
 *
 * In other words, it is used to perform operations that might throw exceptions in a more functional way,
 * providing methods to handle success and failure scenarios cleanly and declaratively.
 *
 * <p>The outcome of such computations can be either a Success<T> where T is the class of the computed value or
 * a Failure<T> which only contains the exception that occurred.
 * <ul>
 *     <li>{@code Success} - Represents a successful computation and contains the computed value.</li>
 *     <li>{@code Failure} - Represents a computation that resulted in an exception and contains the exception
 *     caught.</li>
 * </ul>
 *
 */
public abstract class Try<T> {

  public static <T> Try<T> of(Supplier<T> supplier) {
    return impl(nonNull("supplier", supplier), () -> {});
  }

  public static <T> Try<T> of(Supplier<T> supplier, Runnable finallyStatement) {
    return impl(nonNull("supplier", supplier),  nonNull("finallyStatement", finallyStatement));
  }

  public static <T> Try<T> ofThrowing(ThrowingSupplier<T> supplier) {
    return impl(nonNull("supplier", supplier), () -> {});
  }

  public static <T> Try<T> ofThrowing(ThrowingSupplier<T> supplier, ThrowingRunnable finallyStatement) {
    return impl(nonNull("supplier", supplier),  nonNull("finallyStatement", finallyStatement));
  }

  /**
   * Retrieves the computed value if computation was successful, otherwise throws a TryWrappedException
   * which contains as a cause the exception that was thrown
   *
   * @return the value held by this instance, if available.
   * @throws TryWrappedException if no value is present in this instance.
   */
  public abstract T getOrThrow();

  /**
   * Transforms the value contained in this Try using the given functions depending on the outcome
   * (success or failure). This method applies the {@code successMapper} function to the value if this
   * instance represents a success or the {@code failureMapper} function if it was a failure.
   *
   * <p>Example usage:
   * <pre>
   * Try<String> result = Try.of(() -> "123")
   *                                .map(Integer::parseInt, ex -> -1);  // Returns an integer
   * </pre>
   *
   * @param <U> the type of the result after applying the mapper function.
   * @param successMapper the function to apply to the value if this Try is a {@code Success}.
   * @param failureMapper the function to apply to the encapsulated exception if this Try is a {@code Failure}.
   * @return a new {@code Try<U>} instance containing either the transformed value or result of the failure handling.
   */
  public abstract <U> U map(Function<T, U> successMapper, Function<TryWrappedException, U> failureMapper);

  /**
   * Executes the provided {@code thenable} function with the result of this {@code Try} if it is successful,
   * and returns a new {@code Try} object of type {@code U} as returned by the function.
   *
   * This method allows for the chaining of multiple computational steps, each of which might fail,
   * into a cohesive bundle of code. If this {@code Try} instance is a {@code Success}, the result is
   * passed to the {@code thenable} function; if it is a {@code Failure}, the failure is propagated
   * and the function is not executed.
   *
   * @param <U> the type of the result produced by the thenable function
   * @param thenable a function that takes a value of type {@code T} and returns a {@code Try<U>}.
   *                 This function is only applied if the current {@code Try} is a {@code Success}.
   * @return a new {@code Try<U>} resulting from the application of the {@code thenable} function
   *         if this {@code Try} was successful; otherwise, the original failure is returned.
   */
  public abstract <U> Try<U> andThen(Function<T, Try<U>> thenable);

  /**
   * Converts this Try instance into an {@code Either} representation, where the left side holds
   * a failure (exception) and the right side holds a success (value).
   *
   * This method provides a seamless transition from a Try to an {@code Either} structure, serving scenarios
   * where the explicit differentiation between success and failure is crucial without throwing an exception.
   * It allows further processing where operations may branch more distinctly based on the outcome encapsulated in
   * this Try.
   *
   * @return an {@code Either<TryWrappedException, T>} where the left side contains the exception if the original
   * Try was a failure, and the right side contains the value if the original Try was a success.
   * @param <T> the type of the success value in this Try.
   */
  public abstract Either<TryWrappedException, T> toEither();

  /**
   * Converts this Try instance into an {@code Optional} representation, encapsulating the value if present,
   * otherwise resulting in an empty {@code Optional}.
   *
   * This method facilitates the transition from a Try that might hold a value or an exception to
   * an {@code Optional} that only concerns itself with the presence or absence of a value.
   * It is particularly useful in cases where you wish to discard error details and focus solely on
   * the availability of the result.
   *
   * <p>Example usage:
   * <pre>
   * Try<Integer> result = Try.of(() -> Integer.parseInt("123"));
   * Optional<Integer> optionalResult = result.toOptional();
   * optionalResult.ifPresent(System.out::println);  // Prints the result if present
   * </pre>
   *
   * @return an {@code Optional<T>} containing the value if the original Try was a success,
   * or an empty {@code Optional} if it was a failure.
   * @param <T> the type of the value contained in this Try, if it was successful.
   */
  public abstract Optional<T> toOptional();

  /**
   * Executes the provided {@code action} if this Try instance is a {@code Success}.
   *
   * This method allows for side effects to be performed on the value contained within the Try when it
   * is successful, such as logging, modifying external state, or performing additional computations.
   * It is typically used to react to successful outcomes without altering the structure or the content of
   * the Try.
   *
   * <p>Note: This method does not affect the value within the Try; it only allows for additional operations to
   * be performed. The original Try is returned, allowing for method chaining and
   * further processing.
   *
   * <p>Example usage:
   * <pre>
   * Try<Integer> result = Try.of(() -> Integer.parseInt("123"));
   * result.onSuccess(value -> System.out.println("Parsed successfully: " + value))
   *       .onFailure(e -> System.out.println("Failed to parse: " + e));
   * </pre>
   *
   * @param action the consumer action that will be executed if this Try is a {@code Success}.
   * @return the same Try instance, allowing for further methods chaining.
   * @param <T> the type of the successful result contained within this Try.
   */
  public abstract Try<T> onSuccess(Consumer<T> action);

  /**
   * Executes the provided {@code action} if this Try instance is a {@code Failure}.
   *
   * This method allows for side effects to be performed on the exception contained within the Try when
   * it results in a failure. It's typically used to react to failures, such as logging errors,
   * sending error notifications or performing cleanup tasks.
   *
   * <p>Note: This method does not affect the value within the Try; it only allows for additional operations to
   * be performed. The original Try is returned, allowing for method chaining and
   * further processing.
   *
   * <p>Example usage:
   * <pre>
   * Try<String> result = Try.of(() -> { throw new RuntimeException("Failure"); });
   * result.onFailure(e -> System.out.println("Error: " + e.getMessage()))
   *       .onSuccess(value -> System.out.println("Success: " + value));
   * </pre>
   *
   * @param action the consumer action that will be executed if this Try is a {@code Failure}. This action
   *               should not return a result and should focus on handling or reacting to the exception.
   * @return the same Try instance, allowing for further method chaining.
   * @param <T> the type of the intended result of this Try, not affected by the action.
   */
  public abstract Try<T> onFailure(Consumer<TryWrappedException> action);

  /**
   * Recovers from a failure of this Try by providing a fallback value.
   *
   * This method is used when you want to ensure that the Try returns a value even in case of failure,
   * effectively replacing the exception with a specified default value. It is particularly useful in scenarios where
   * continuing the computation with a known safe value is preferable to handling an exception.
   *
   * <p>Example usage:
   * <pre>
   * Try<Integer> result = Try.of(() -> { throw new ArithmeticException("division by zero"); });
   * Integer safeResult = result.recoverWithValue(0);  // Provides a default value in case of failure
   * System.out.println("Result: " + safeResult);
   * </pre>
   *
   * @param value the default value to use in case the original Try is a {@code Failure}.
   * @return the original value if this Try is a {@code Success}, or the provided value otherwise.
   * @param <T> the type of the value held by this Try and the type of the fallback value.
   */
  public abstract T recoverWithValue(T value);

  /**
   * Recovers from a failure of this Try by applying a specified function to the encapsulated exception.
   *
   * This method provides a way to handle exceptions dynamically by converting them into a valid result.
   * It is useful in scenarios where the recovery process needs to be tailored based on the type of the exception
   * encountered.
   *
   * <p>Example usage:
   * <pre>
   * Try<Integer> result = Try.of(() -> { throw new ArithmeticException("division by zero"); });
   * Integer recoveredResult = result.recover(ex -> {
   *     if (ex instanceof ArithmeticException) return 1;  // Default value for division by zero
   *     return 0;  // General fallback for other exceptions
   * });
   * System.out.println("Recovered Result: " + recoveredResult);
   * </pre>
   *
   * @param recoverFunction the function to apply to the exception if this Try is a {@code Failure}.
   *                        This function should return a new value of type {@code T}, effectively replacing the
   *                        failure with a recovery result.
   * @return the original value if this Try is a {@code Success}, or a new value generated by the
   * {@code recoverFunction} if it is a {@code Failure}.
   * @param <T> the type of the value held by this Try and the type of the result returned by the recover
   *           function.
   */
  public abstract T recover(Function<TryWrappedException, T> recoverFunction);

  /**
   * Checks whether this Try instance is a success or not.
   *
   * Although this method provides a straightforward way to decide code flow, however it should not be used frequently.
   *
   * <p>Example code like below SHOULD BE discouraged:
   * <pre>
   * Try<String> result = Try.of(() -> "Hello, world!");
   * if (result.hasSucceeded()) {
   *     System.out.println("Operation was successful!");
   * } else {
   *     System.out.println("Operation failed.");
   * }
   * </pre>
   *
   * A better version of the above is:
   * <pre>
   * Try<String> result = Try.of(() -> "Hello, world!")
   *                         .onSuccess(val -> System.out.println("Operation was successful!"))
   *                         .onFailure(ex -> System.out.println("Operation failed."))
   * </pre>
   *
   * @return {@code true}, if it is a {@code Success} or {@code false} otherwise
   */
  public abstract boolean hasSucceeded();

  /**
   * Checks whether this Try instance is a failure or not.
   *
   * Although this method provides a straightforward way to decide code flow, however it should not be used frequently.
   *
   * <p>Example code like below SHOULD BE discouraged:
   * <pre>
   * Try<String> result = Try.of(() -> "Hello, world!");
   * if (result.hasSucceeded()) {
   *     System.out.println("Operation was successful!");
   * } else {
   *     System.out.println("Operation failed.");
   * }
   * </pre>
   *
   * A better version of the above is:
   * <pre>
   * Try<String> result = Try.of(() -> "Hello, world!")
   *                         .onSuccess(val -> System.out.println("Operation was successful!"))
   *                         .onFailure(ex -> System.out.println("Operation failed."))
   * </pre>
   *
   * @return {@code true}, if it is a {@code Failure} or {@code false} otherwise
   */
  public abstract boolean hasFailed();

  /**
   * Retrieves the timestamp of when the operation encapsulated by this Try was attempted.
   *
   * This method is useful for logging, auditing or any scenario where tracking the exact time of when the operation
   * occurred is important.
   *
   * <p>Example usage:
   * <pre>
   * Try<String> result = Try.of(() -> "Operation attempt");
   * System.out.println("Tried at: " + result.getTriedAt());
   * </pre>
   *
   * @return an {@code Instant} representing the timestamp right before execution occurs.
   */
  public abstract Instant getTriedAt();

  private static <T> Try<T> impl(Supplier<T> supplier, Runnable finallyStatement) {

    final var triedAt = Instant.now();
    Try<T> tryToReturn;

    try {
      tryToReturn = new Success<>(triedAt, supplier.get());
    } catch (Exception ex) {
      tryToReturn = new Failure<>(
        triedAt,
        new TryWrappedException((ex instanceof WrappedCheckedException) ? ex.getCause() : ex));
    } finally {
      finallyStatement.run();
    }
    return tryToReturn;
  }

  public static final class Success<T> extends Try<T> {

    @Override
    public T getOrThrow() {
        return value;
    }

    @Override
    public <U> U map(Function<T, U> mapper, Function<TryWrappedException, U> failureMapper) {
      return mapper.apply(value);
    }

    @Override
    public <U> Try<U> andThen(Function<T, Try<U>> thenable) {
      return thenable.apply(value);
    }

    @Override
    public Try<T> onSuccess(Consumer<T> action) {
      action.accept(value);
      return this;
    }

    @Override
    public Try<T> onFailure(Consumer<TryWrappedException> action) {
      return this;
    }

    @Override
    public T recover(Function<TryWrappedException, T> recoverFunction) {
      return value;
    }

    @Override
    public T recoverWithValue(T newValue) {
      return value;
    }

    @Override
    public boolean hasSucceeded() {
      return true;
    }

    @Override
    public boolean hasFailed() {
      return false;
    }

    @Override
    public Either<TryWrappedException, T> toEither() {
      return Either.right(value);
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.of(value);
    }

    @Override
    public Instant getTriedAt() {
      return triedAt;
    }

    private Success(Instant triedAt, T value) {
      this.triedAt = triedAt;
      this.value = value;
    }

    private final T value;
    private final Instant triedAt;
  }

  public static final class Failure<T> extends Try<T> {

    @Override
    public T getOrThrow() {
      throw tryWrappedException;
    }

    @Override
    public <U> U map(Function<T, U> mapper, Function<TryWrappedException, U> failureMapper) {
      return failureMapper.apply(tryWrappedException);
    }

    @Override
    public <U> Try<U> andThen(Function<T, Try<U>> thenable) {
      return new Failure<>(triedAt, tryWrappedException);
    }

    @Override
    public Try<T> onSuccess(Consumer<T> action) {
      return this;
    }

    @Override
    public Try<T> onFailure(Consumer<TryWrappedException> action) {
      action.accept(tryWrappedException);
      return this;
    }

    @Override
    public T recover(Function<TryWrappedException, T> recoverFunction) {
      return recoverFunction.apply(tryWrappedException);
    }

    @Override
    public T recoverWithValue(T value) {
      return value;
    }

    @Override
    public boolean hasSucceeded() {
      return false;
    }

    @Override
    public boolean hasFailed() {
      return true;
    }

    @Override
    public Either<TryWrappedException, T> toEither() {
      return Either.left(tryWrappedException);
    }

    @Override
    public Optional<T> toOptional() {
      return empty();
    }

    @Override
    public Instant getTriedAt() {
      return triedAt;
    }

    private Failure(Instant triedAt, TryWrappedException tryWrappedException) {
      this.triedAt = triedAt;
      this.tryWrappedException = tryWrappedException;
    }

    private final TryWrappedException tryWrappedException;
    private final Instant triedAt;
  }

  public static final class TryWrappedException extends RuntimeException {
    public TryWrappedException(Throwable caughtException) {
      super(caughtException);
    }
  }

  public static <A, B> TryCollector2<A, B> collect(
    Try<A> aTry, Try<B> bTry) {

    return new TryCollector2<>(aTry, bTry);
  }

  public static <A, B, C> TryCollector3<A, B, C> collect(
    Try<A> aTry, Try<B> bTry, Try<C> cTry) {

    return new TryCollector3<>(aTry, bTry, cTry);
  }

  public static <A, B, C, D> TryCollector4<A, B, C, D> collect(
    Try<A> aTry, Try<B> bTry, Try<C> cTry, Try<D> dTry) {

    return new TryCollector4<>(aTry, bTry, cTry, dTry);
  }

  public static <A, B, C, D, E> TryCollector5<A, B, C, D, E> collect(
    Try<A> aTry, Try<B> bTry, Try<C> cTry, Try<D> dTry, Try<E> eTry) {

    return new TryCollector5<>(aTry, bTry, cTry, dTry, eTry);
  }

  public static final class TryCollector2<A, B> {

    public <R> R allOrThrow(BiFunction<A, B, R> successHandler) {
      return all(successHandler, ex -> { throw ex; });
    }

    public <R> R all(BiFunction<A, B, R> successHandler, Function<TryWrappedException, R> exceptionHandler) {

      if (aTry.hasSucceeded()) {
        if (bTry.hasSucceeded()) {
          return successHandler.apply(aTry.getOrThrow(), bTry.getOrThrow());
        } else {
          return exceptionHandler.apply(exposeException(bTry));
        }
      } else {
        return exceptionHandler.apply(exposeException(aTry));
      }
    }

    private TryCollector2(Try<A> aTry, Try<B> bTry) {
      this.aTry = aTry;
      this.bTry = bTry;
    }

    private final Try<A> aTry;
    private final Try<B> bTry;
  }

  public static final class TryCollector3<A, B, C> {

    public <R> R allOrThrow(TriFunction<A, B, C, R> successHandler) {
      return all(successHandler, ex -> { throw ex; });
    }

    public <R> R all(TriFunction<A, B, C, R> successHandler, Function<TryWrappedException, R> exceptionHandler) {

      if (aTry.hasSucceeded()) {
        if (bTry.hasSucceeded()) {
          if (cTry.hasSucceeded()) {
            return successHandler.apply(
              aTry.getOrThrow(),
              bTry.getOrThrow(),
              cTry.getOrThrow());
          } else {
            return exceptionHandler.apply(exposeException(cTry));
          }
        } else {
          return exceptionHandler.apply(exposeException(bTry));
        }
      } else {
        return exceptionHandler.apply(exposeException(aTry));
      }
    }

    private TryCollector3(Try<A> aTry, Try<B> bTry, Try<C> cTry) {
      this.aTry = aTry;
      this.bTry = bTry;
      this.cTry = cTry;
    }

    private final Try<A> aTry;
    private final Try<B> bTry;
    private final Try<C> cTry;
  }

  public static final class TryCollector4<A, B, C, D> {

    public <R> R allOrThrow(QuadFunction<A, B, C, D, R> successHandler) {
      return all(successHandler, ex -> { throw ex; });
    }

    public <R> R all(QuadFunction<A, B, C, D, R> successHandler, Function<TryWrappedException, R> exceptionHandler) {

      if (aTry.hasSucceeded()) {
        if (bTry.hasSucceeded()) {
          if (cTry.hasSucceeded()) {
            if (dTry.hasSucceeded()) {
              return successHandler.apply(
                aTry.getOrThrow(),
                bTry.getOrThrow(),
                cTry.getOrThrow(),
                dTry.getOrThrow());
            } else {
              return exceptionHandler.apply(exposeException(dTry));
            }
          } else {
            return exceptionHandler.apply(exposeException(cTry));
          }
        } else {
          return exceptionHandler.apply(exposeException(bTry));
        }
      } else {
        return exceptionHandler.apply(exposeException(aTry));
      }
    }

    private TryCollector4(Try<A> aTry, Try<B> bTry, Try<C> cTry, Try<D> dTry) {
      this.aTry = aTry;
      this.bTry = bTry;
      this.cTry = cTry;
      this.dTry = dTry;
    }

    private final Try<A> aTry;
    private final Try<B> bTry;
    private final Try<C> cTry;
    private final Try<D> dTry;
  }

  public static final class TryCollector5<A, B, C, D, E> {

    public <R> R allOrThrow(QuintFunction<A, B, C, D, E, R> successHandler) {
      return all(successHandler, ex -> { throw ex; });
    }

    public <R> R all(
      QuintFunction<A, B, C, D, E, R> successHandler,
      Function<TryWrappedException, R> exceptionHandler) {

      if (aTry.hasSucceeded()) {
        if (bTry.hasSucceeded()) {
          if (cTry.hasSucceeded()) {
            if (dTry.hasSucceeded()) {
              if (eTry.hasSucceeded()) {
                return successHandler.apply(
                    aTry.getOrThrow(),
                    bTry.getOrThrow(),
                    cTry.getOrThrow(),
                    dTry.getOrThrow(),
                    eTry.getOrThrow());
              } else {
                return exceptionHandler.apply(exposeException(eTry));
              }
            } else {
              return exceptionHandler.apply(exposeException(dTry));
            }
          } else {
            return exceptionHandler.apply(exposeException(cTry));
          }
        } else {
          return exceptionHandler.apply(exposeException(bTry));
        }
      } else {
        return exceptionHandler.apply(exposeException(aTry));
      }
    }

    private TryCollector5(Try<A> aTry, Try<B> bTry, Try<C> cTry, Try<D> dTry, Try<E> eTry) {
      this.aTry = aTry;
      this.bTry = bTry;
      this.cTry = cTry;
      this.dTry = dTry;
      this.eTry = eTry;
    }

    private final Try<A> aTry;
    private final Try<B> bTry;
    private final Try<C> cTry;
    private final Try<D> dTry;
    private final Try<E> eTry;
  }

  private static <T> TryWrappedException exposeException(Try<T> t) {
    return ((Failure<T>)t).tryWrappedException;
  }
}
