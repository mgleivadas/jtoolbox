package com.brontoblocks;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.concurrent.ThreadLocalRandom.current;

public class RandomSource {

    public static int nextInt() {
        return current().nextInt();
    }

    public static int nextInt(int lower, int upper) {
        return current().nextInt(lower, upper);
    }

    public static int nextIntClosed(int lower, int upper) {
        return current().nextInt(lower, upper + 1);
    }

    public static long nextLong() {
        return current().nextLong();
    }

    public static long nextLong(long lower, long upper) {
        return current().nextLong(lower, upper);
    }

    public static long nextLongClosed(long lower, long upper) {
        return current().nextLong(lower, upper + 1);
    }

    public static int randomIntOneOf(int... t) {
        return t[nextInt(0, t.length)];
    }

    public static long randomLongOneOf(long... t) {
        return t[nextInt(0, t.length)];
    }

    public static double randomDoubleOneOf(double... t) {
        return t[nextInt(0, t.length)];
    }

    public static <T> T randomObjOneOf(T... t) {
        return t[nextInt(0, t.length)];
    }

    public static <T> T randomObjOneOf(List<T> t) {
        return t.get(nextInt(0, t.size()));
    }

    public static IntStream randomStreamFrom(int... t) {
        return IntStream.generate(() -> randomIntOneOf(t));
    }

    public static LongStream randomStreamFrom(long... t) {
        return LongStream.generate(() -> randomLongOneOf(t));
    }
}
