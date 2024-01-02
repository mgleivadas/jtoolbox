package com.brontoblocks.time;

import com.brontoblocks.tuple.Pair;

import java.time.Duration;

import static java.lang.Math.addExact;

public abstract class TimeKeeper {

  public static TimeKeeper createWithRealTimeKeeping() {
    return RealTimeKeeper.INSTANCE;
  }

  public abstract long getNanoTime();

  private static final class RealTimeKeeper extends TimeKeeper {

    private static final RealTimeKeeper INSTANCE = new RealTimeKeeper();

    @Override
    public long getNanoTime() {
      return System.nanoTime();
    }
  }

  public static final class LinearTimeKeeper extends TimeKeeper {

    public static LinearTimeKeeper of(Duration duration) {
      return new LinearTimeKeeper(duration);
    }

    private LinearTimeKeeper(Duration duration) {
      this.tickDelta = duration.toNanos();
      this.timeInfo = Pair.of(0L, 0L);
    }

    public long registerNewTick() {
      var timeInfoLocal = timeInfo;
      var tickCount = timeInfoLocal.second();
      var newTick = addExact(timeInfoLocal.first(), tickDelta);
      timeInfo = Pair.of(newTick, ++tickCount);
      return tickCount;
    }

    private final long tickDelta;
    private volatile Pair<Long, Long> timeInfo;

    @Override
    public long getNanoTime() {
      return timeInfo.first();
    }
  }
}
