package com.brontoblocks.thread;

import static com.brontoblocks.utils.ArgCheck.noNegativeLong;
import static com.brontoblocks.utils.RandomSource.randomIntOneOf;

public final class ThreadUtils {

    public static void sleepInMillis(long millis) {
        noNegativeLong("milliseconds", millis);

        try {
            Thread.sleep(millis);
        } catch (Throwable t) {
            // Catch spurious interrupts. No Further action
        }
    }

    public static void sleepInSeconds(int seconds) {
        sleepInMillis(seconds * 1_000L);
    }

    public static void sleepSomeMillisCloseTo(long millis) {

        var multiplier = randomIntOneOf(0, -1, 1);
        var extraMillis = randomIntOneOf(1, 2, 3);

        var total = Math.min(1, millis + ((long) multiplier * extraMillis));
        sleepInMillis(total);
    }

    public static Thread newDaemonThread(String name, Runnable runnable) {
        var t = new Thread(runnable);
        t.setName(name);
        t.setDaemon(true);
        return t;
    }

    public static Thread newThread(String name, Runnable runnable) {
        var t = new Thread(runnable);
        t.setName(name);
        t.setDaemon(false);
        return t;
    }

    public static void runWithDelay(Runnable runnable, long runAfter) {
        new Thread(() -> {
            sleepInMillis(runAfter);
            runnable.run();
        }).start();
    }
}
