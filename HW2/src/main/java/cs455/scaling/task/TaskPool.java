package cs455.scaling.task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskPool {

    private TaskWorker[] workers;
    private PriorityQueue<Task> queue;
    private Integer[] statuses;

    private static final int STATUS_IDLE = 0;
    private static final int STATUS_ACTIVE = 1;

    public TaskPool(int numThreads) {
        this.workers = new TaskWorker[numThreads];
        this.queue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return 0;
            }
        });
        init();
    }

    private void init() {
        for (int i = 0; i < this.workers.length; i++) {
            workers[i] = new TaskWorker(i, this);
            workers[i].start();
        }
        this.statuses = new Integer[this.workers.length];
        for (int i = 0 ; i < this.workers.length ; i++) {
            statuses[i] = STATUS_IDLE;
        }
        new Thread(() -> run()).start();
    }

    public void addTask(Task t) {
        synchronized (queue) {
            queue.add(t);
            queue.notify();
        }
    }

    public void run() {
        while (true) {
            try {
                for (int i = 0 ; i < this.statuses.length ; i++) {
                    synchronized (this.statuses[i]) {
                        if (this.statuses[i] == STATUS_ACTIVE)
                            continue;
                        else {
                            this.statuses[i] = STATUS_ACTIVE;
                            this.workers[i].setTask(getTask());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Task Pool run method error: " + e.getMessage());
            }
        }
    }

    private Task getTask() throws Exception {
        Task t;
        synchronized (queue) {
            while (queue.peek() == null)
                queue.wait();

            t = queue.poll();
            queue.notify();
        }
        return t;
    }

    public void notify(int id) {
        this.statuses[id] = STATUS_IDLE;
    }
}
