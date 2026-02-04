package br.com.luizgustavosgobi.simpleServer.core;

import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class ThreadManager {
    private final ExecutorService ioExecutor;
    private final ExecutorService computeExecutor;
    private final ScheduledExecutorService scheduleExecutor;

    public ThreadManager() {
        this.ioExecutor = Executors.newVirtualThreadPerTaskExecutor();
        this.computeExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.scheduleExecutor = Executors.newScheduledThreadPool(0);
    }

    public <T> CompletableFuture<T> submitToIO(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, ioExecutor);
    }

    public CompletableFuture<Void> submitToIO(Runnable task) {
        return CompletableFuture.runAsync(task, ioExecutor);
    }

    public <T> CompletableFuture<T> submitToCompute(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, computeExecutor);
    }

    public CompletableFuture<Void> submitToCompute(Runnable task) {
        return CompletableFuture.runAsync(task, computeExecutor);
    }

    public void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit timeUnit) {
        scheduleExecutor.scheduleAtFixedRate(task, initialDelay, period, timeUnit);
    }

    public void shutdown() {
        ioExecutor.shutdown();
        computeExecutor.shutdown();

        try {
            if (!ioExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                ioExecutor.shutdownNow();
            }

            if (!computeExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                computeExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Logger.Error(this, "Error while waiting for Executors to terminate: " + e.getMessage());
        }
    }
}