package cs455.scaling.task;

import java.util.PriorityQueue;

public class TaskPool {

    private PriorityQueue<Task> queue;

    public TaskPool() {
        this.queue = new PriorityQueue<>();
    }

    public synchronized Task pop() {
        return this.queue.poll();
    }

    public synchronized void put(Task t) {
        this.queue.add(t);
    }

    public synchronized int size() {
        return this.queue.size();
    }

}
