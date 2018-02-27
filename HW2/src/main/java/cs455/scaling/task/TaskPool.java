package cs455.scaling.task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskPool {

    private PriorityQueue<Task> queue;
    private TaskWorker[] workers;

    public TaskPool(int numThreads) {
        this.queue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return 0;
            }
        });
        this.workers = new TaskWorker[numThreads];
        init();
    }

    private void init() {
        for (int i = 0 ; i < this.workers.length ; i++) {
            workers[i] = new TaskWorker();
            workers[i].start();
        }
    }

    public void addTask(Task t) {
        synchronized (queue) {
            this.queue.add(t);
            this.queue.notify();
        }
    }

}
