package server;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {
    private final LinkedList<Runnable> taskQueue = new LinkedList<>();
    private final Thread[] workers;
    private final Lock lock = new ReentrantLock();
    private final Condition workerCondition = lock.newCondition();
    private volatile boolean isRunning = true;

    public ThreadPool(int poolSize) {
        workers = new Thread[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    public void submitTask(Runnable task) {
        lock.lock();
        try {
            if (!isRunning) throw new IllegalStateException("Thread pool already closed.");
            taskQueue.add(task);
            workerCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            isRunning = false;
            workerCondition.signalAll();
        } finally {
            lock.unlock();
        }
        
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            while (true) {
                Runnable task;
                lock.lock();
                try {
                    while (taskQueue.isEmpty() && isRunning) {
                        try {
                            workerCondition.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    if (!isRunning && taskQueue.isEmpty()) {
                        return;
                    }

                    task = taskQueue.poll();
                } finally {
                    lock.unlock();
                }

                if (task != null) {
                    try {
                        task.run();
                    } catch (RuntimeException e) {
                        System.err.println("Error execution: " + e.getMessage());
                    }
                }
            }
        }
    }
}