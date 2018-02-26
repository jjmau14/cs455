package cs455.scaling.task;

public class TaskExecuter implements Runnable {

    private TaskWorker[] workers;

    public TaskExecuter(TaskWorker[] workers) {
        this.workers = workers;
    }

    public void run() {
        while(true) {

        }
    }
}
