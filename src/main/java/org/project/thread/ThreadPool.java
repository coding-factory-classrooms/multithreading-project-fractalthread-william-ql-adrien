package org.project.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool implements ExecutorService {

    private BlockingQueue<Runnable> runnableQueue;
    private List<WorkerThread> threads;
    private AtomicBoolean isThreadPoolShutDownInitiated;

    public ThreadPool(int noOfThreads) {
        this.runnableQueue = new LinkedBlockingQueue<>();
        this.threads = new ArrayList<>(noOfThreads);
        this.isThreadPoolShutDownInitiated = new AtomicBoolean(false);

        for (int i = 1; i <= noOfThreads; i++) {
            WorkerThread thread = new WorkerThread(runnableQueue, this);
            thread.setName("Worker Thread - " + i);
            thread.start();
            threads.add(thread);
        }

    }

    @Override
    public void execute(Runnable r){
        if (!isThreadPoolShutDownInitiated.get()) {
            try {
                runnableQueue.put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() {
        isThreadPoolShutDownInitiated = new AtomicBoolean(true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return isThreadPoolShutDownInitiated.get();
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        for (WorkerThread workerThread : threads) {
            workerThread.join(timeUnit.toMillis(l));
        }
        return true;
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return null;
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T t) {
        return null;
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    private static class WorkerThread extends Thread {
        // holds tasks
        private BlockingQueue<Runnable> taskQueue;
        // check if shutdown is initiated
        private ThreadPool threadPool;
        public WorkerThread(BlockingQueue<Runnable> taskQueue, ThreadPool threadPool) {
            this.taskQueue = taskQueue;
            this.threadPool = threadPool;
        }
        @Override
        public void run() {
            try {
                // continue until all tasks finished processing
                while (!threadPool.isThreadPoolShutDownInitiated.get() || !taskQueue.isEmpty()) {
                    Runnable r;
                    // Poll a runnable from the queue and execute it
                    while ((r = taskQueue.poll()) != null) {
                        r.run();
                    }
                    Thread.sleep(1);
                }
            } catch (RuntimeException | InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}