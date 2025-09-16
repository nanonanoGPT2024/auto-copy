package com.scheduler.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleThreadTaskQueue {

    private final BlockingQueue<Runnable> taskQueue;
    private final Thread workerThread;

    public SingleThreadTaskQueue() {
        taskQueue = new LinkedBlockingQueue<>();
        workerThread = new Thread(() -> {
            while (true) {
                try {
                    // Take and execute the next task in the queue
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // Exit the loop if the thread is interrupted
                }
            }
        });
        workerThread.start();
    }

    public void submitTask(Runnable task) {
        taskQueue.offer(task);
    }

    public void shutdown() {
        workerThread.interrupt();
    }

    public int getQueueSize() {
        return taskQueue.size(); // Returns the number of tasks in the queue
    }
}
