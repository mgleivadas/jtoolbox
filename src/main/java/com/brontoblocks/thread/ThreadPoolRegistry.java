package com.brontoblocks.thread;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static com.brontoblocks.utils.ArgCheck.noNegativeInt;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ThreadPoolRegistry<THREAD_POOL_NAMES extends Enum<THREAD_POOL_NAMES>> {

    public ThreadPoolRegistry() {
        this.threadPools = new ConcurrentHashMap<>();
    }

    public int getSystemCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public Optional<ExecutorService> requestThreadPool(THREAD_POOL_NAMES threadPoolName) {
        return Optional.ofNullable(threadPools.get(threadPoolName.name()));
    }

    public void storeThreadPool(THREAD_POOL_NAMES threadPoolName, int minThreadPoolSize, int maxThreadPoolSize) {
        storeThreadPool(threadPoolName, minThreadPoolSize, maxThreadPoolSize, 20);
    }

    public void storeThreadPool(THREAD_POOL_NAMES threadPoolName, int minThreadPoolSize, int maxThreadPoolSize, int threadLifeTimeInSeconds) {
        var minSize = noNegativeInt("minThreadPoolSize", minThreadPoolSize);
        var maxSize = noNegativeInt("maxThreadPoolSize", maxThreadPoolSize);

        var threadPool = new ThreadPoolExecutor(minSize, maxSize, 20L, SECONDS, new SynchronousQueue<>());

        threadPools.compute(threadPoolName.name(), (k, v) -> {
            if (v == null) {
                return threadPool;
            } else {
                throw new RuntimeException("Thread pool: %s already exists".formatted(threadPoolName));
            }
        });
    }

    private final Map<String, ThreadPoolExecutor> threadPools;
}
