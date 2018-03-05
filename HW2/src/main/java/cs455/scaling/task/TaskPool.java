package cs455.scaling.task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TaskPool {

    private TaskWorker[] workers;
    private TaskExecuter executer;

    public TaskPool(int numThreads) {
        this.workers = new TaskWorker[numThreads];
        init();
    }

    private void init() {
        for (int i = 0; i < this.workers.length; i++) {
            workers[i] = new TaskWorker();
            workers[i].start();
        }
        new Thread(() -> this.executer = new TaskExecuter(this.workers)).start();
    }

    public void addTask(Task t) {
        this.executer.addTask(t);
    }
}
