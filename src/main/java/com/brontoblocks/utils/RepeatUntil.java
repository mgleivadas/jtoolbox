package com.brontoblocks.utils;

import static com.brontoblocks.thread.ThreadUtils.sleepInMillis;
import static com.brontoblocks.utils.ExceptionUtils.eitherValueOrCapture;

public class RepeatUntil<T> {

    public static <T> RepeatUntil<T> of(ValueProducer<T> valueProducer,
                                        FinishPredicate finishPredicate,
                                        BackoffRetryMechanism backoff) {

        return new RepeatUntil<>(valueProducer, finishPredicate, backoff);
    }


    protected RepeatUntil(ValueProducer<T> valueProducer, FinishPredicate finishPredicate, BackoffRetryMechanism backoff) {
        this.producerFunction = valueProducer;
        this.finPredicate = finishPredicate;
        this.backoff = backoff;
    }

    public T start() {

        int numOfTries = 0;
        boolean shouldRetry;
        do  {

            final var curTry = numOfTries;
            Either<Throwable, T> res = eitherValueOrCapture(() -> producerFunction.produce(curTry));
            if (res.hasRightSide()) {
                return res.getRight();
            } else {
                shouldRetry = finPredicate.shouldContinue(res.getLeft(), numOfTries);
            }

            sleepInMillis(backoff.retry(numOfTries++));
        } while (shouldRetry);

        throw new RepeatUntilException("Maximum number of tries reached.");
    }

    private final ValueProducer<T> producerFunction;
    private final FinishPredicate finPredicate;
    private final BackoffRetryMechanism backoff;


    public static class CappedExponentialBackoff implements BackoffRetryMechanism {

        public static BackoffRetryMechanism of(long cap, long baseMillis) {
            return new CappedExponentialBackoff(cap, baseMillis);
        }

        protected CappedExponentialBackoff(long cap, long baseMillis) {
            this.cap = cap;
            this.baseMillis = baseMillis;
        }

        private final long cap;
        private final long baseMillis;

        @Override
        public long retry(int numOfTries) {
            var exponent = 2 ^ numOfTries;
            return Math.min(cap, exponent * baseMillis);
        }
    }


    @FunctionalInterface
    public interface BackoffRetryMechanism {
        long retry(int numOfTries);
    }

    @FunctionalInterface
    public interface ValueProducer<T> {
        T produce(int numOfTries) throws Exception;
    }

    @FunctionalInterface
    public interface FinishPredicate {
        boolean shouldContinue(Throwable exception, int numTries);
    }

    public static final class RepeatUntilException extends RuntimeException {
        public RepeatUntilException(String message) {
            super(message);
        }

        public RepeatUntilException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
