package cs455.scaling.task;

import cs455.scaling.util.TaskQueue;

public class TaskWorker extends Thread {

    private final TaskQueue queue;
    private final int id;

    public TaskWorker(TaskQueue queue, int id) {
        this.queue = queue;
        this.id = id;
    }

    public void run() {
        while(true) {
            Task t = queue.poll();

            t.run();
        }
    }

    public String toString() {
        return "Worker-" + id;
    }

}