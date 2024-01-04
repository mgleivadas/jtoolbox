package simulation;

import com.brontoblocks.chrono.TimeKeeper;
import com.brontoblocks.mutable.MutableInt;
import com.brontoblocks.simulation.PoissonEventGenerator;
import com.brontoblocks.simulation.PoissonEventGenerator.TimeUnit;
import com.brontoblocks.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.brontoblocks.chrono.TimeKeeper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PoissonEventGeneratorTest {

  @ParameterizedTest
  @MethodSource("convergenceScenarios")
  void verify_that_PoissonEventGenerator_converges_to_expected_lambda_rates_successfully(
      Duration clockTickSize,
      Pair<Long, TimeUnit> poissonInfo,
      long clockCycles,
      long expectedNumberOfEvents,
      long iterations,
      double deviationPercentageAccepted,
      double passRateRequired) {

    final var experimentResults = LongStream.rangeClosed(1, iterations)
        .mapToObj(i -> {
          // Given
          final var timeKeeper = LinearTimeKeeper.of(clockTickSize);

          // Creates a Poisson process which registers X million events per Y time unit
          var poissonEventGenerator = PoissonEventGenerator.builder(poissonInfo.first(), poissonInfo.second())
              .withTimeKeeper(timeKeeper)
              .build();

          poissonEventGenerator.resetAndInit();

          // When
          final var result = LongStream.range(1L, clockCycles)
              .map(clockCycle -> {
                timeKeeper.registerNewTick(); // advance time
                return poissonEventGenerator.getNumberOfEventsOccurred();
              })
              .summaryStatistics();

          // Then
          final var eventDifference = Math.abs(expectedNumberOfEvents - result.getSum());
          return Pair.of(100 * (eventDifference / (double) expectedNumberOfEvents), deviationPercentageAccepted);
        })
        .toList();

    final var iterationsSucceeded = experimentResults.stream().filter(p -> p.first() < p.second()).count();
    final var passRate = 100.0 * (iterationsSucceeded / (double) iterations);

    assertTrue(passRate >= passRateRequired,
        "PassRate:%.4f below PassRateRequired:%.4f".formatted(passRate, passRateRequired));
  }

  @Test
  public void verify_randomness_pre_creation_calls_prng_the_correct_number_of_times() {

    // Given
    var size = 20;
    MutableInt counter = MutableInt.of(0);
    PoissonEventGenerator.UniformPrng prng = () -> {
      counter.increaseByOne();
      return 0.1;
    };

    // When
    PoissonEventGenerator.builder(1, TimeUnit.DAY_1)
        .withUniformPrngProvider(prng)
        .enablePreBuffering(size)
        .build();

    // Then
    assertEquals(size, counter.getValue());
  }


  @Test
  public void verify_that_if_PRNG_returns_zero_then_its_being_called_again_for_a_new_value() {

    // Given
    final var retries = 2;
    final MutableInt counter = MutableInt.of(0);
    final PoissonEventGenerator.UniformPrng prng = () -> {
      counter.increaseByOne();
      if (counter.getValue() < retries) {
        return 0.0;
      } else {
        return 0.99;
      }
    };

    // When
     PoissonEventGenerator.builder(1, TimeUnit.DAY_1)
         .withUniformPrngProvider(prng)
         .enablePreBuffering(1)
         .build();

    // Then
    assertEquals(retries, counter.getValue());
  }


  @Test
  public void verify_that_preBuffering_recycles_probabilities_once_limit_is_reached() {

    // Given
    final var capacityLimit = 2;
    final MutableInt counter = MutableInt.of(0);

    // A large tick size to ensure the generation of a new event.
    final var timeKeeper = TimeKeeper.LinearTimeKeeper.of(Duration.ofMillis(100_000));

    final PoissonEventGenerator.UniformPrng prng = () -> {
      counter.increaseByOne();
      return 0.99;
    };

    // When
    var poissonEventGenerator = PoissonEventGenerator.builder(100, TimeUnit.MILLIS_1)
        .withUniformPrngProvider(prng)
        .withTimeKeeper(timeKeeper)
        .enablePreBuffering(capacityLimit)
        .build();

    // And
    timeKeeper.registerNewTick();

    var eventsRetrieved = 0L;
    while (eventsRetrieved < 10L) {
      eventsRetrieved += poissonEventGenerator.getNumberOfEventsOccurred();
      timeKeeper.registerNewTick();
    }

    // Then
    assertEquals(capacityLimit, counter.getValue());
  }


  private static Stream<Arguments> convergenceScenarios() {

    return Stream.of(
        // TimeUnit: MILLIS_1
        Arguments.of(
            Duration.ofMillis(1),                      // ClockTickSize
            Pair.of(1L, TimeUnit.MILLIS_1),            // PoissonInfo
            120_000L,                                  // ClockCycles: Run for 120 second
            120_000L,                                  // ExpectedNumberOfEvents
            20L,                                       // Iterations
            0.8,                                       // DeviationPercentageAccepted (0.8%)
            99.5),                                     // PassRateRequired (99.5%)

        // TimeUnit: HALF_HOUR
        Arguments.of(
            Duration.ofSeconds(2),                     // ClockTickSize
            Pair.of(45_000L, TimeUnit.HALF_HOUR),      // PoissonInfo
            7_200L,                                    // ClockCycles: Run for 4 hour (4 hour in seconds /ClockTickSize)
            360_000L,                                  // ExpectedNumberOfEvents
            20L,                                       // Iterations
            0.8,                                       // DeviationPercentageAccepted (0.8%)
            99.5),                                     // PassRateRequired (99.5%)

        // TimeUnit: MINUTE
        Arguments.of(
            Duration.ofMillis(10),                     // ClockTickSize
            Pair.of(6_000_000L, TimeUnit.MINUTE_1),    // PoissonInfo
            1_000L,                                    // ClockCycles: Run for 10 seconds
            1_000_000L,                                // ExpectedNumberOfEvents: 10 seconds/1 minutes = 1/6 * 6.000.000
            20L,                                       // Iterations
            0.8,                                       // DeviationPercentageAccepted (0.8%)
            99.5),                                     // PassRateRequired (99.5%)

        // TimeUnit: MILLIS_10
        Arguments.of(
            Duration.ofNanos(500_000),                 // ClockTickSize
            Pair.of(75L, TimeUnit.MILLIS_10),          // PoissonInfo
            240_000L,                                  // ClockCycles: Run for 2 mins. 2 mins/500microns=240.000 cycles
            900_000L,                                  // ExpectedNumberOfEvents: 2 minutes/10millis = 12.000 * 75
            20L,                                       // Iterations
            0.8,                                       // DeviationPercentageAccepted (0.8%)
            99.5),                                     // PassRateRequired (99.5%)

        // TimeUnit: SECOND_45
        Arguments.of(
            Duration.ofSeconds(1),                     // ClockTickSize
            Pair.of(5_000L, TimeUnit.SECOND_45),       // PoissonInfo
            1_800L,                                    // ClockCycles: Run for 30 mins. 30 mins/1sec = 1800 cycles
            200_000L,                                  // ExpectedNumberOfEvents: 30 minutes/45sec = 40 * 5000
            20L,                                       // Iterations
            0.8,                                       // DeviationPercentageAccepted (0.8%)
            99.5)                                      // PassRateRequired (99.5%)
    );
  }
}