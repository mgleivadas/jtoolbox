package com.brontoblocks.utils;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class NumberUtils {

    public static Optional<Integer> tryInt(String val) {
        try {
            return of(Integer.valueOf(val));
        } catch (NumberFormatException ex) {
            return empty();
        }
    }

    public static Optional<Integer> tryPositiveOrZeroInt(String val) {
        try {
            Integer x = Integer.valueOf(val);
            return (x < 0)
                ? empty()
                : of(x);
        } catch (NumberFormatException ex) {
            return empty();
        }
    }

    public static Optional<Long> tryLong(String val) {
        try {
            return of(Long.valueOf(val));
        } catch (NumberFormatException ex) {
            return empty();
        }
    }

    public static Optional<Double> tryDouble(String val) {
        try {
            return of(Double.valueOf(val));
        } catch (NumberFormatException ex) {
            return empty();
        }
    }

    public static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(value);
    }

}
