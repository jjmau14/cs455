package cs455.scaling.task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskExecuter implements Runnable {

    private TaskWorker[] workers;
    private PriorityQueue<Task> queue;

    public TaskExecuter(TaskWorker[] workers) {
        this.workers = workers;
        this.queue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return 0;
            }
        });
    }

    public void addTask(Task t) {
        synchronized (this.queue) {
            queue.add(t);
            queue.notify();
        }
    }

    public void run() {
        while(true) {
            // Pop a task to assign
            Task t;

            try {
                synchronized (this.queue) {
                    while (queue.peek() == null) {
                        queue.wait();
                    }
                    t = queue.poll();
                    boolean assigned = false;
                    while(!assigned) {
                        for (TaskWorker worker : workers) {
                            if (worker.getStatus() == 0) {
                                worker.setTask(t);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ;
            }
        }
    }
}