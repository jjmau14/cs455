package cs455.scaling.task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskPool {

    private TaskWorker[] workers;
    private PriorityQueue<Task> queue;

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
            workers[i] = new TaskWorker();
            workers[i].start();
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
            // Pop a task to assign
            Task t;

            try {
                System.out.println("OK");
                synchronized (queue) {
                    while (queue.peek() == null)
                        queue.wait();
                    System.out.println("OK");

                    t = queue.poll();
                    queue.notify();
                }
                System.out.println("OK");

                boolean assigned = false;
                while (!assigned) {                System.out.println("OK");

                    for (TaskWorker worker : workers) {
                        if (worker.setTask(t) != -1) {
                            assigned = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Task Pool run method error: " + e.getMessage());
            }
        }
    }
}
