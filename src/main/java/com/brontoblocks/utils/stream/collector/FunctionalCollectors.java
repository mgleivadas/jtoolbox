package com.brontoblocks.utils.stream.collector;

import com.brontoblocks.mutable.MutableOptionalReference;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.function.Function.identity;

public final class FunctionalCollectors {

  /**
   * A function that accumulates elements from left to right in a strictly ordered, sequential process.
   * Unlike a traditional {@code reduce} operation in Java streams, {@code foldLeft} guarantees left-to-right processing,
   * making it suitable for operations that depend on order-sensitive accumulation.
   * <p>
   * Java's streams provide a {@code reduce} method to accumulate elements into a single result. However, {@code reduce}
   * is designed with parallel processing in mind and generally requires that the accumulator function is associative,
   * meaning that reordering elements does not affect the result. This is because {@code reduce} may apply optimizations
   * that divide elements into chunks, process them independently, and combine results. While efficient for parallel
   * operations, these optimizations mean that {@code reduce} does not guarantee strictly left-to-right processing,
   * even in sequential streams.
   * <p>
   * In contrast, {@code foldLeft} emulates left-fold behavior commonly found in functional programming languages,
   * where each element is accumulated strictly in order. This makes it suitable for operations where ordering is
   * crucial, such as building strings from prefixes, applying non-associative operations, or accumulating mutable
   * states that depend on element order.
   * Finally, {@code foldLeft} does not support parallel execution because it would violate the
   * left-to-right ordering requirement.
   *
   * <pre>
   *  List<String> words = List.of("Java", "is", "awesome");
   *  int maxWordChars = words.stream().collect(foldLeft(0, (currentMax, word) -> max(currentMax, word.length)));
   *                   => 7
   * </pre>
   *
   * @param <E> the type of elements in the stream
   * @param <A> the type of the accumulator (initial and result type for the operation)
   * @param <R> the result type after the finishing transformation
   */
  public static <E, A, R> FoldLeftCollector<E, A, R> foldLeft(
    A initial,
    BiFunction<A, E, A> accumulator,
    Function<A, R> finisher
  ) {
    return new FoldLeftCollector<>(initial, accumulator, finisher);
  }

  /**
   * Semantically equivalent to {@link FunctionalCollectors#foldLeft }. The difference is that no finisher parameter
   * is needed as the accumulated value is the final computed value.
   */
  public static <E, A> FoldLeftCollector<E, A, A> foldLeft(
    A initial,
    BiFunction<A, E, A> accumulator) {
    return foldLeft(initial, accumulator, identity());
  }

  public static final class FoldLeftCollector<E, A, R> implements Collector<E, MutableOptionalReference<A>, R> {

    @Override
    public Supplier<MutableOptionalReference<A>> supplier() {
      return () -> MutableOptionalReference.of(initial);
    }

    @Override
    public BiConsumer<MutableOptionalReference<A>, E> accumulator() {
      return (MutableOptionalReference<A> accumulatedValueRef, E element) -> {
        accumulatedValueRef.setValue(accumulator.apply(accumulatedValueRef.getReferenceOrThrow(), element));
      };
    }

    @Override
    public Function<MutableOptionalReference<A>, R> finisher() {
      return accumulatedValue -> finisher.apply(accumulatedValue.getReferenceOrThrow());
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }

    @Override
    public BinaryOperator<MutableOptionalReference<A>> combiner() {
      return (ref, ref2) -> {
        throw new UnsupportedOperationException(
          "Parallel stream processing is not allowed for 'foldLeft' as that would violate semantics");
      };
    }

    private FoldLeftCollector(
      A initial,
      BiFunction<A, E, A> accumulator,
      Function<A, R> finisher) {
      this.initial = initial;
      this.accumulator = accumulator;
      this.finisher = finisher;
    }

    private final A initial;
    private final BiFunction<A, E, A> accumulator;
    private final Function<A, R> finisher;
  }

}
