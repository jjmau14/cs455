package cs455.scaling.util;

import cs455.scaling.task.Task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskQueue {

    private PriorityQueue<Task> queue;

    public TaskQueue() {
        this.queue = new PriorityQueue(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 0;
            }
        });
    }

    public Task poll() {
        Task t = new Task(null, null);
        try {
            synchronized (this) {
                while (this.queue.size() == 0) {
                    wait();
                }
                t = queue.poll();
            }
        } catch (Exception e) {}

        return t;
    }

    public void put(Task t) {
        synchronized (this) {
            this.queue.add(t);
            notify();
        }
    }

}
