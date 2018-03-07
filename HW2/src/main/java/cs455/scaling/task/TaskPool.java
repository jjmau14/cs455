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
        new Thread(this::run).start();
    }

    public void addTask(Task t) {
        synchronized (queue) {
            //System.out.println("Added Task");
            queue.add(t);
            queue.notify();
        }
    }

    public void run() {
        while (true) {
            // Pop a task to assign
            Task t;

            try {
                synchronized (queue) {
                    while (queue.peek() == null) {
                        queue.wait();
                    }
                    t = queue.poll();
                    //System.out.println("Task popped");
                    boolean assigned = false;
                    while (!assigned) {
                        int i = 0;
                        for (TaskWorker worker : workers) {
                            i += 1;
                            if (worker.setTask(t) == 1) {
                                assigned = true;
                                break;
                            }
                        }
                        if (assigned) {
                            System.out.println("Task assigned to " + i);
                        }
                    }
                }
            } catch (Exception e) {
                ;
            }
        }
    }
}
