package com.brontoblocks;

import static java.lang.String.format;

public final class ArgCheck {

    public static <T> T nonNull(String argName, T obj) {

        if (obj == null) {
            throw new IllegalArgumentException(format("Argument:%s failed 'null' check.", argName));
        }
        return obj;
    }

    public static int noNegativeInt(String argName, int arg) {

        if (arg < 0) {
            throw new IllegalArgumentException(format("Argument:%s failed 'non-negative' check.", argName));
        }

        return arg;
    }

    public static long noNegativeLong(String argName, long arg) {

        if (arg < 0) {
            throw new IllegalArgumentException(format("Argument:%s failed 'non-negative' check.", argName));
        }

        return arg;
    }

    public static String nonEmpty(String argName, String arg) {

        if (arg == null || arg.length() == 0) {
            throw new IllegalArgumentException(format("Argument:%s failed 'non-empty' check.", argName));
        }

        return arg;
    }

    public static int exact(String argName, int arg, int expectedValue) {

        if (arg != expectedValue) {
            throw new IllegalArgumentException(format("Argument:%s failed 'equality' check.", argName));
        }

        return arg;
    }

    public static long inRange(String argName, long numToTest, long lowerBoundary, long upperBoundary,
                                  RangeArgCheckMode mode) {

        if (numToTest < lowerBoundary || numToTest > upperBoundary) {
            throw new IllegalArgumentException(format("Argument:%s invalid. Failed 'in-range' check (not in range).", argName));
        }

        if (numToTest == lowerBoundary && (mode == RangeArgCheckMode.EXCLUSIVE_EXCLUSIVE || mode == RangeArgCheckMode.EXCLUSIVE_INCLUSIVE)) {
            throw new IllegalArgumentException(format("Argument:%s invalid. Failed 'in-range' check (lower-boundary).", argName));
        }

        if (numToTest == upperBoundary && (mode == RangeArgCheckMode.EXCLUSIVE_EXCLUSIVE || mode == RangeArgCheckMode.INCLUSIVE_EXCLUSIVE)) {
            throw new IllegalArgumentException(format("Argument:%s invalid. Failed 'in-range' check (upper-boundary).", argName));
        }

        return numToTest;
    }

    public static int inRange(String argName, int numToTest, int lowerBoundary, int upperBoundary,
                                  RangeArgCheckMode mode) {

        if (numToTest < lowerBoundary || numToTest > upperBoundary) {
            throw new IllegalArgumentException(format("Argument:%s invalid. Failed 'in-range' check (not in range).", argName));
        }

        if (numToTest == lowerBoundary && (mode == RangeArgCheckMode.EXCLUSIVE_EXCLUSIVE || mode == RangeArgCheckMode.EXCLUSIVE_INCLUSIVE)) {
            throw new IllegalArgumentException(format("Argument:%s invalid. Failed 'in-range' check (lower-boundary).", argName));
        }

        if (numToTest == upperBoundary && (mode == RangeArgCheckMode.EXCLUSIVE_EXCLUSIVE || mode == RangeArgCheckMode.INCLUSIVE_EXCLUSIVE)) {
            throw new IllegalArgumentException(format("Argument:%s invalid. Failed 'in-range' check (upper-boundary).", argName));
        }

        return numToTest;
    }

    public static <T> T oneOf(String argName, T arg, T... options) {

        for (T option : options) {
            if (option.equals(arg)) {
                return arg;
            }
        }

        throw new IllegalArgumentException(format("Argument:%s failed options check.", argName));
    }

    public enum RangeArgCheckMode {
        INCLUSIVE_INCLUSIVE,
        INCLUSIVE_EXCLUSIVE,
        EXCLUSIVE_INCLUSIVE,
        EXCLUSIVE_EXCLUSIVE
    }
}
