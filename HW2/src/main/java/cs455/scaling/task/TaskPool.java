package cs455.scaling.task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskPool {

    private PriorityQueue<Task> queue;
    private worker[] workers;

    public TaskPool(int numThreads) {
        this.queue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return 0;
            }
        });
        this.workers = new worker[numThreads];
        init();
    }

    private void init() {
        for (int i = 0 ; i < this.workers.length ; i++) {
            workers[i] = new worker();
            workers[i].start();
        }
    }

    private class worker extends Thread {

        private Task task;

        public worker() {
            this.task = null;
        }

        public void run() {
            while (true) {
                synchronized (task) {
                    if (task == null) {
                        try {
                            task.wait();
                        } catch (Exception e) {

                        }
                    }
                    task.run();
                    task = null;
                }
            }
        }

        public void setTask(Task task) {
            synchronized (this.task) {
                this.task = task;
                this.task.notify();
            }
        }
    }

    public void addTask(Task t) {
        synchronized (queue) {
            this.queue.add(t);
            this.queue.notify();
        }
    }

}
