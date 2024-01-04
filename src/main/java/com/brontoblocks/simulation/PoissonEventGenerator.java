package com.brontoblocks.simulation;

import com.brontoblocks.chrono.TimeKeeper;

import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static com.brontoblocks.utils.ArgCheck.RangeArgCheckMode.INCLUSIVE_INCLUSIVE;
import static com.brontoblocks.utils.ArgCheck.*;

/**
 * This class is a handy utility class that can be used to track the event rate generation backed by a Poisson process.
 * A Poisson process is a form of a stochastic process. That is, it is characterized by specific probability
 * distribution, yet when you observe single small changes of the process these seem to be random.
 * <p>
 * An example of a Poisson process is when someone rolls the dice every second and calculates how many times he gets six
 * over the period of 1 hour. Since 1 hour has 3600 seconds and a dice has 6 outcomes that means he will get a favorable
 * outcome every 6 seconds or 3600/6 = 600/hour. Notice that if we measure the number of dice rolls between two
 * favorable events (the distance) then we won't get 6 all the time. We will be getting a different 'seemingly random'
 * number.
 * <p>
 * When we try to simulate a system in which events register at a specific rate, Poisson process is the best modelling
 * tool we can use. It allows us to create a 'well-behaved' random process which mimics accurately real world scenarios.
 * <p>
 * The following section describes how the formulas that are used across this class were derived. It is written for
 * maintainers or for those who would like to understand on how everything works. However, it is not necessary, nor it
 * is expected to read it. If you only want to use this class go to USAGE section.
 * <p>
 * EXPLANATION
 * ===========
 * This section is not a replacement for a maths textbook in order to explain everything about Poisson processes.
 * For those who are curious there is enough material online for that. Here, the Poisson process definition and its
 * formula are considered 'axioms' and we will just help the reader to understand the rest of the journey.
 * From the definition to class formulas. In addition, the reader is assumed to be familiar with some basic
 * mathematical concepts (as all software engineers should be).
 * <p>
 * By definition a Poisson process consists of a sequence of random variables. All of them need to follow the same
 * distribution, the Poisson distribution. It is given by the following probability mass function (PMF):
 * <p>
 * P(n, l) = (e^-l * l^n) / n! (1)
 * <p>
 * The above formula calculates the probability of n events occurring in some arbitrary referenced unit time (RUT),
 * given that, the average occurrence rate is l within a single RUT.
 * e.g. If the average rate of rainy days per month is 5 days/month, what is the probability of getting "exactly" 10
 * days of rain next month ?
 * P(10, 5) = (e^-5 * 5^10) / 10! = 1.81 % (not very likely)
 * But if calculate for 4 days then:
 * P(4, 5) = (e^-5 * 5^4) / 4! = 17.54 % (much more likely since 4, is closer to the expected value of 5)
 * <p>
 * However, (1) only refers to the probability that a specific number of events will occur in a single RUT.
 * What if we want to calculate for more than 1 RUT ?
 * (e.g. The probability of getting 12 rainy days in a period of 6 months ?)
 * <p>
 * We can simply define a new RUT, which is a multiple of the previous one. So, RUT_2 = t * RUT_1.
 * Then we can use (1) by replacing where l with t * l, thus obtaining a function for calculating event occurrence
 * probability over multiple 'referenced unit times'.
 * <p>
 * P(n, l, t) = (e^(-t*l) * (t*l)^n) / n! (2)
 * <p>
 * (e.g. The probability of raining "exactly" 12 days in a period of 6 months, where each month has on
 * average 5 days/month rain is: P(12, 5, 6) = 0.01 % (extremely unlikely)
 * However, if again calculate for 30 days then P(30, 5, 6) = 7.26 %
 * (much more likely since it is again closer to the expected value)
 * <p>
 * Rewriting (2) ignoring lambda constant and parameter n, thus focusing only in time t:
 * P(t) = (e^(-t*l) * (t*l)^n) / n! (2.1)
 * <p>
 * Now we can construct a function which will give us the inter arrival times between two consecutive events.
 * With Ti we denote the arrival times that the ith event occurs. The 1st arrives at T1, the 2nd at T2, etc.
 * <p>
 * We start our observation at an arbitrary time which for us is going to be our starting point (denoted with 0)
 * and we are interested to know when the next event will arrive. Let's assume the first event arrives at time T1.
 * The probability that an event will arrive at some point in time T1 is equal to the negated probability of NOT
 * arriving prior to T1.
 * <p>
 * From 2.1, if we set n = 0, meaning what is the probability no events to occur until time t. Then we have
 * P(0, l, t) = e^(-t*l). So the probability of NOT getting an event in a range (0, t] is decreasing exponentially.
 * The exponential curve is controlled not only by time but by lambda constant as well.
 * The faster the rate per RUT the faster it will drop the probability of not getting an event as time passes.
 * <p>
 * However, we need the negated probability so:
 * P(T1 > t) = 1 - P(0, l, t) => P(T1 > t) = 1 - e^(-t*l) when t > 0 or 0 otherwise. (2.2)
 * <p>
 * In addition, because we chose an arbitrary point in time to be our starting point, the above formula
 * not only describes the probability of getting the first event but rather it calculates the probability
 * of getting an event after a previous one has occurred. Similarly to T1, that was defined as the time
 * between the beginning of measuring and first arrival, T2 could be regarded the same as T1, had we started our
 * experiment at time T1 (T1 becomes 0 and T2 now becomes T1). Another way to visualize this concept is by checking
 * the following time slots. A1 is the time slot when first event occurred, A2 the second, etc.
 * Note: the event occurs always on the right boundary
 * <p>
 * A1 = (0, T1], A2 = (T1, T2], A3 = (T2, T3] <=> A1 = (T(k-1), T(k)], A2 = (T(k), T(k+1)], A3 = (T(k+1), T(k+2)]
 * Its obvious that formula 2.2 holds regardless of where we will start our measuring. So formula (2.2) gives us the
 * probability of getting an event after elapsed time t (expressed in RUT) since the previous one.
 * <p>
 * <p>
 * Finally, we have a function that takes inter-arrival time as a scalar value as input and returns the probability of
 * getting the next event in that timeframe. If we invert the 2.2 so that we feed a specific probability then we could
 * take the inter-arrival time as a result.
 * <p>
 * The inverse function of 2.2 is: F(p) = -ln(1-p)/l (3)
 * By feeding a uniform distribution between (0, 1) to simulate that an event 'indeed happened' when the probability
 * was predicted to be P then F(p=P) will return the appropriate inter-arrival time for such an event to occur.
 * <p>
 * The pseudocode to get next event inter-arrival time:
 * 1) Obtain some random probability: p = prng()
 * 2) nextArrival = F(p) = -ln(1-p)/l
 * <p>
 * Remember the nextArrival is expressed in RUT so some adjustment maybe needed according to implementation.
 * <p>
 *
 * USAGE
 * =====
 * In order to use PoissonEventGenerator there are two mandatory variables that need to be specified.
 * The rate, which is the number of events that should be generated every referenced time unit.
 * The timeUnit, which is the frame of reference regarding time.
 * Together these variables specify how many events will be generated over a period of time.
 * e.g. 100 events per 2 hours.
 *
 * When you want to model something like 1 event per minute it is better to model it as 60 per hour.
 * On the other hand if you need to generate 86_400_000 events per day it probably makes much more sense
 * to express it as 60_000 per minute. Overall, TimeUnit has enough time periods predefined to cover all scenarios.
 * If you can't find, a specific time period, probably it was purposefully omitted to force you use an alternative one.
 *
 * However, no matter what TimeUnit you use, the precision is the same for all combinations.
 * The results returned from this class remain 100% accurate at all times. Don't confuse real accuracy
 * with "perceived accuracy". If you run an experiment just a couple of iterations, then the results you will get
 * might not be what you expect. This has nothing to do with accuracy.
 *
 * e.g.
 * If you toss a coin 3 times you will get one side 66.6% and the other one 33.3%. This outcome does not imply that
 * tossing a normal coin does not have 50% to get landed on either side.
 *
 * Usage scenarios
 * ===============
 *
 * 1) Generates 100 events per hour
 *    PoissonEventGenerator.builder(100, HOUR).build()
 *
 *
 * 2) Generates 1000 events per millis, using pre-buffering for extra speed and a custom PRNG
 *    var customPRNG;
 *    PoissonEventGenerator.builder(1000, MILLIS_1)
 *        .withUniformPrngProvider(customPRNG)
 *        .enablePreBuffering(100_000_000)
 *        .build()
 *
 *
 */
public final class PoissonEventGenerator {

  public static PoissonEventGeneratorBuilder builder(long rate, TimeUnit timeUnit) {

    return new PoissonEventGeneratorBuilder(
        inRange("rate", rate, 1, Long.MAX_VALUE, INCLUSIVE_INCLUSIVE),
        nonNull("timeUnit", timeUnit));
  }

  private PoissonEventGenerator(PoissonEventGeneratorConfiguration config) {
    this.config = config;
    this.eventBeingHeld = OptionalLong.empty();
  }

  /**
   * This method calculates the inter arrival time between two consecutive events
   * which are part of a Poisson process with l = this.rate
   * @return The number of events that have occurred since the last time that this method was invoked.
   */
  public long getNumberOfEventsOccurred() {

    final var nowTime = config.timeKeeper().getNanoTime();
    var eventsOccurred = 0;

    var eventBeingHeldCached = eventBeingHeld;
    var lastReportedEventCached = lastReportedEvent;

    if (eventBeingHeldCached.isPresent()) {
      final var eventBeingHeldElapsedTime = eventBeingHeldCached.getAsLong() - nowTime;
      if (eventBeingHeldElapsedTime > 0) {
        return 0;
      } else {
        eventsOccurred++;
        lastReportedEventCached = eventBeingHeldCached.getAsLong();
        eventBeingHeldCached = OptionalLong.empty();
      }
    }

    while (eventBeingHeldCached.isEmpty()) {

      final var nextEventInterArrivalTime = config.poissonInterArrivalTimesProvider.getNextInterArrivalInNanos();
      final var nextEventArrival = lastReportedEventCached + nextEventInterArrivalTime;
      if (nextEventArrival <= nowTime) {
        eventsOccurred++;
        lastReportedEventCached = nextEventArrival;
      } else {
        eventBeingHeldCached = OptionalLong.of(nextEventArrival);
      }
    }

    eventBeingHeld = eventBeingHeldCached;
    lastReportedEvent = lastReportedEventCached;

    return eventsOccurred;
  }

  public void resetAndInit() {
    eventBeingHeld = OptionalLong.empty();
    lastReportedEvent = config.timeKeeper().getNanoTime();
  }

  private volatile OptionalLong eventBeingHeld;
  private volatile long lastReportedEvent;
  private final PoissonEventGeneratorConfiguration config;

  private record PoissonEventGeneratorConfiguration(
      PoissonInterArrivalTimesProvider poissonInterArrivalTimesProvider,
      TimeKeeper timeKeeper
  ) { }

  public static final class PoissonEventGeneratorBuilder {

    private PoissonEventGeneratorBuilder(long rate, TimeUnit timeUnit) {
      this.rate = rate;
      this.timeUnit = timeUnit;
      this.timeKeeper = STD_TIMEKEEPER;
      this.uniformPrng = wrapPrngWithZeroCheck(STD_PRNG);
      this.preBufferingSize = -1;
    }

    public PoissonEventGeneratorBuilder withTimeKeeper(TimeKeeper timeKeeper) {
      this.timeKeeper = nonNull("timeKeeper", timeKeeper);
      return this;
    }

    public PoissonEventGeneratorBuilder withUniformPrngProvider(UniformPrng uniformPrng) {
      this.uniformPrng = wrapPrngWithZeroCheck(nonNull("uniformPrng", uniformPrng));
      return this;
    }

    public PoissonEventGeneratorBuilder enablePreBuffering(int size) {
      this.preBufferingSize = noNegativeInt("size", size);
      return this;
    }

    public PoissonEventGenerator build() {

      if (preBufferingSize > 0) {
        return new PoissonEventGenerator(
            new PoissonEventGeneratorConfiguration(
                new BufferedPoissonInterArrivalTimesProvider(
                    () -> getNextInterArrivalInNanos(uniformPrng, rate, timeUnit.getRutInNanos()),
                    preBufferingSize),
                timeKeeper));
      } else {
        return new PoissonEventGenerator(
            new PoissonEventGeneratorConfiguration(
                () -> getNextInterArrivalInNanos(uniformPrng, rate, timeUnit.getRutInNanos()),
                timeKeeper));
      }
    }

    private final long rate;
    private final TimeUnit timeUnit;
    private TimeKeeper timeKeeper;
    private UniformPrng uniformPrng;
    private int preBufferingSize;

    private static final TimeKeeper STD_TIMEKEEPER = TimeKeeper.createWithRealTimeKeeping();

    private static long getNextInterArrivalInNanos(
        UniformPrng uniformPRNG,
        long rate,
        long rutInNanos) {

      final var randomness = uniformPRNG.getNext();
      return Math.round(-Math.log(1 - randomness) / rate * rutInNanos);
    }

    private static UniformPrng wrapPrngWithZeroCheck(UniformPrng uniformPrng) {
      return () -> {
        var res = 0.0;
        while (res == 0.0) {
          res = uniformPrng.getNext();
        }
        return res;
      };
    }

    private static final UniformPrng STD_PRNG = ThreadLocalRandom.current()::nextDouble;
  }

  public enum TimeUnit {

    MILLIS_1(1),                // 1 MILLISECOND
    MILLIS_2(2),                // 2 MILLISECONDS
    MILLIS_3(3),                // 3 MILLISECONDS
    MILLIS_5(5),                // 5 MILLISECONDS
    MILLIS_7(7),                // 7 MILLISECONDS
    MILLIS_10(10),              // 10 MILLISECONDS
    MILLIS_11(11),              // 11 MILLISECONDS
    MILLIS_13(13),              // 13 MILLISECONDS
    MILLIS_17(17),              // 17 MILLISECONDS
    MILLIS_19(19),              // 19 MILLISECONDS

    SECOND_1(1_000),            // 1000 MILLISECONDS
    SECOND_3(3_000),            // 3000 MILLISECONDS
    SECOND_5(5_000),            // 5000 MILLISECONDS
    SECOND_45(45_000),          // 45000 MILLISECONDS

    MINUTE_1(60_000),           // 60000 MILLISECONDS
    MINUTE_7(420_000),          // 420_000 MILLISECONDS

    QUARTER_HOUR(900_000),      // 900_000 MILLISECONDS
    HALF_HOUR(1_800_000),       // 1_800_000 MILLISECONDS
    HOUR(3_600_000),            // 3_600_000 MILLISECONDS
    HOURS_3(10_800_000),        // 10_800_000 MILLISECONDS
    HOURS_5(18_000_000),        // 18_000_000 MILLISECONDS
    HOURS_7(25_200_000),        // 25_200_000 MILLISECONDS

    DAY_1(86_400_000);          // 86_400_000 MILLISECONDS

    public long getRutInNanos() {
      return nanos;
    }

    TimeUnit(long millis) {
      this.nanos = millis * MILLIS_TO_NANOS_MULTIPLIER;
    }

    private final long nanos;
    private static final long MILLIS_TO_NANOS_MULTIPLIER = 1_000_000;
  }

  /**
   * An implementation of a PRNG which returns values from (0.0 - 1.0) uniformly.
   */
  @FunctionalInterface
  public interface UniformPrng {
    double getNext();
  }

  @FunctionalInterface
  public interface PoissonInterArrivalTimesProvider {
    long getNextInterArrivalInNanos();
  }

  private static final class BufferedPoissonInterArrivalTimesProvider implements PoissonInterArrivalTimesProvider {

    private BufferedPoissonInterArrivalTimesProvider(
        PoissonInterArrivalTimesProvider poissonInterArrivalTimesProvider,
        int size) {

      this.size = size;
      this.idx = 0;
      this.randomnessBuffer = IntStream.range(0, size)
          .mapToObj(i -> poissonInterArrivalTimesProvider.getNextInterArrivalInNanos())
          .toList();
    }

    @Override
    public long getNextInterArrivalInNanos() {
      if (idx == size)
        idx = 0;
      return randomnessBuffer.get(idx++);
    }

    private final int size;
    private final List<Long> randomnessBuffer;
    private volatile int idx;
  }

}
