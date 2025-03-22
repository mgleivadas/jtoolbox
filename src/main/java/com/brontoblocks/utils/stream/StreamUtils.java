package com.brontoblocks.utils.stream;

import com.brontoblocks.utils.stream.spliterators.BatchSpliterator;
import com.brontoblocks.utils.stream.spliterators.SkipUntilSpliterator;
import com.brontoblocks.utils.stream.spliterators.TakeWhileSpliterator;
import com.brontoblocks.utils.stream.spliterators.ZippingSpliterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public final class StreamUtils {

    public static <T> Stream<T> roundRobin(T... elements) {
        return roundRobin(asList(elements));
    }

    public static <T> Stream<T> roundRobin(List<T> elements) {
        return IntStream.iterate(0, i -> i == elements.size() - 1 ? 0 : i + 1).mapToObj(elements::get);
    }

    public static <T> Stream<T> fromIterator(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return stream(iterable.spliterator(), false);
    }
    public static <T> List<T> closeableToList(Supplier<Stream<T>> producer) {
        try (Stream<T> t = producer.get()) {
            return t.collect(toList());
        }
    }

    public static <T> Collection<T> closeableToCollection(Supplier<Stream<T>> producer,
                                                          Supplier<Collection<T>> supplier) {
        try (Stream<T> t = producer.get()) {
            return t.collect(toCollection(supplier));
        }
    }

    public static <T> void consumeCloseableByElement(Supplier<Stream<T>> producer, Consumer<T> consumer) {
        try (Stream<T> t = producer.get()) {
            t.forEachOrdered(consumer);
        }
    }

    public static <T> void consumeCloseable(Supplier<Stream<T>> producer, Consumer<Stream<T>> consumer) {
        try (Stream<T> t = producer.get()) {
            consumer.accept(t);
        }
    }

    public static <T, R> R mapCloseable(Supplier<Stream<T>> producer, Function<Stream<T>, R> function) {
        try (Stream<T> t = producer.get()) {
            return function.apply(t);
        }
    }

    public static <I extends InputStream, O extends OutputStream> void copy(I input, O out, byte[] buffer) {

        try {
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> Stream<T> reject(Stream<T> source, Predicate<? super T> pred) {
        return source.filter(pred.negate());
    }

    public static <T> Stream<Batch<T>> batchify(Stream<T> stream, int batchSize) {
        return batchSize <= 0
                ? Stream.empty()
                : stream(new BatchSpliterator<>(stream.spliterator(), batchSize), false);
    }

    public static <T> Stream<Indexed<T>> zipWithIndex(Stream<T> source) {
        return zip(indices().boxed(), source, Indexed::new);
    }

    public static <T, U, R> Stream<R> zip(Stream<T> lefts, Stream<U> rights, BiFunction<T, U, R> combiner) {
        return stream(ZippingSpliterator.of(lefts.spliterator(), rights.spliterator(), combiner), false);
    }

    public static <T> Stream<T> takeWhile(Stream<T> source, Predicate<T> pred, boolean inclusive) {
        return StreamSupport.stream(new TakeWhileSpliterator<>(source.spliterator(), pred, inclusive), false);
    }

    public static <T> Stream<T> skipUntil(Stream<T> source, Predicate<T> pred, boolean inclusive) {
        return StreamSupport.stream(new SkipUntilSpliterator<>(source.spliterator(), pred, inclusive), false);
    }

    private static LongStream indices() {
        return LongStream.iterate(0L, l -> l + 1);
    }
}
