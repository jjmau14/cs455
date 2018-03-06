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
                }
                t = queue.poll();
                //System.out.println("Task popped");
                boolean assigned = false;
                while (!assigned) {
                    for (TaskWorker worker : workers) {
                        if (worker.getStatus() == 0) {
                            //System.out.println("Assigning task");
                            worker.setTask(t);
                            assigned = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                ;
            }
        }
    }
}
