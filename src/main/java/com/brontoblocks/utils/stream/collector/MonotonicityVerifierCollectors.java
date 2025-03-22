package com.brontoblocks.utils.stream.collector;

import java.util.stream.Collector;

public final class MonotonicityVerifierCollectors {

  public static Collector<Integer, IntSequenceChecker, Boolean> verifyAscendingOrderOfIntegers() {
    return Collector.of(
      IntSequenceChecker::new,
      IntSequenceChecker::acceptWhileAscending,
      IntSequenceChecker::combine,
      IntSequenceChecker::isMonotonic
    );
  }

  public static Collector<Integer, IntSequenceChecker, Boolean> verifyDescendingOrderOfIntegers() {
    return Collector.of(
      IntSequenceChecker::new,
      IntSequenceChecker::acceptWhileDescending,
      IntSequenceChecker::combine,
      IntSequenceChecker::isMonotonic
    );
  }

  public static Collector<Long, LongSequenceChecker, Boolean> verifyAscendingOrderOfLongs() {
    return Collector.of(
      LongSequenceChecker::new,
      LongSequenceChecker::acceptWhileAscending,
      LongSequenceChecker::combine,
      LongSequenceChecker::isMonotonic
    );
  }

  public static Collector<Long, LongSequenceChecker, Boolean> verifyDescendingOrderOfLongs() {
    return Collector.of(
      LongSequenceChecker::new,
      LongSequenceChecker::acceptWhileDescending,
      LongSequenceChecker::combine,
      LongSequenceChecker::isMonotonic
    );
  }

  public static Collector<Float, FloatSequenceChecker, Boolean> verifyAscendingOrderOfFloats() {
    return Collector.of(
      FloatSequenceChecker::new,
      FloatSequenceChecker::acceptWhileAscending,
      FloatSequenceChecker::combine,
      FloatSequenceChecker::isMonotonic
    );
  }

  public static Collector<Float, FloatSequenceChecker, Boolean> verifyDescendingOrderOfFloats() {
    return Collector.of(
      FloatSequenceChecker::new,
      FloatSequenceChecker::acceptWhileDescending,
      FloatSequenceChecker::combine,
      FloatSequenceChecker::isMonotonic
    );
  }

  public static Collector<Double, DoubleSequenceChecker, Boolean> verifyAscendingOrderOfDoubles() {
    return Collector.of(
      DoubleSequenceChecker::new,
      DoubleSequenceChecker::acceptWhileAscending,
      DoubleSequenceChecker::combine,
      DoubleSequenceChecker::isMonotonic
    );
  }

  public static Collector<Double, DoubleSequenceChecker, Boolean> verifyDescendingOrderOfDoubles() {
    return Collector.of(
      DoubleSequenceChecker::new,
      DoubleSequenceChecker::acceptWhileDescending,
      DoubleSequenceChecker::combine,
      DoubleSequenceChecker::isMonotonic
    );
  }

  public static final class IntSequenceChecker {

    public void acceptWhileAscending(int current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current > previous;
        }
        previous = current;
      }
    }

    public void acceptWhileDescending(int current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current < previous;
        }
        previous = current;
      }
    }

    public IntSequenceChecker combine(IntSequenceChecker other) {
      throw new IllegalStateException("Parallel execution not supported");
    }

    public boolean isMonotonic() {
      return isMonotonic;
    }

    private IntSequenceChecker() {
      this.previous = null;
      this.isMonotonic = true;
    }

    private Integer previous;
    private boolean isMonotonic;
  }

  public static final class LongSequenceChecker {

    public void acceptWhileAscending(long current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current > previous;
        }
        previous = current;
      }
    }

    public void acceptWhileDescending(long current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current < previous;
        }
        previous = current;
      }
    }

    public LongSequenceChecker combine(LongSequenceChecker other) {
      throw new IllegalStateException("Parallel execution not supported");
    }

    public boolean isMonotonic() {
      return isMonotonic;
    }

    private LongSequenceChecker() {
      this.previous = null;
      this.isMonotonic = true;
    }

    private Long previous;
    private boolean isMonotonic;
  }

  public static final class FloatSequenceChecker {

    public void acceptWhileAscending(float current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current > previous;
        }
        previous = current;
      }
    }

    public void acceptWhileDescending(float current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current < previous;
        }
        previous = current;
      }
    }

    public FloatSequenceChecker combine(FloatSequenceChecker other) {
      throw new IllegalStateException("Parallel execution not supported");
    }

    public boolean isMonotonic() {
      return isMonotonic;
    }

    private FloatSequenceChecker() {
      this.previous = null;
      this.isMonotonic = true;
    }

    private Float previous;
    private boolean isMonotonic;
  }
  public static final class DoubleSequenceChecker {

    public void acceptWhileAscending(double current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current > previous;
        }
        previous = current;
      }
    }

    public void acceptWhileDescending(double current) {
      if (isMonotonic) {
        if (previous != null) {
          isMonotonic = current < previous;
        }
        previous = current;
      }
    }

    public DoubleSequenceChecker combine(DoubleSequenceChecker other) {
      throw new IllegalStateException("Parallel execution not supported");
    }

    public boolean isMonotonic() {
      return isMonotonic;
    }

    private DoubleSequenceChecker() {
      this.previous = null;
      this.isMonotonic = true;
    }

    private Double previous;
    private boolean isMonotonic;
  }
}
