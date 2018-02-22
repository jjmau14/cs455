package cs455.scaling.task;

import java.util.PriorityQueue;

public class TaskPool {

    private PriorityQueue<Task> queue;

    public TaskPool() {
        this.queue = new PriorityQueue<>();
    }

    public Task pop() {
        synchronized (queue) {
            if (this.queue.peek() == null) {
                System.out.println("Task Pool waiting...");
                try {
                    queue.wait();
                } catch (Exception e) {}
            }
            return this.queue.poll();
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
