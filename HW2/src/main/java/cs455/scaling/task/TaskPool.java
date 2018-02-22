package cs455.scaling.task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskPool {

    private PriorityQueue<Task> queue;

    public TaskPool() {
        this.queue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return 0;
            }
        });
    }

    public Task pop() {
        synchronized (queue) {
            if (this.queue.peek() == null) {
                System.out.println("Task Pool waiting...");
                try {
                    queue.wait();
                } catch (Exception e) {}
            }
            Task t = queue.poll();
            queue.notify();
            return t;
        }
    }

    public void put(Task t) {
        synchronized (queue) {
            this.queue.add(t);
            this.queue.notify();
        }
    }

    public synchronized int size() {
        return this.queue.size();
    }

}
