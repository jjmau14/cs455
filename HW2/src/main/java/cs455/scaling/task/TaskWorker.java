package cs455.scaling.task;

public class TaskWorker extends Thread {

    private Task task;
    private Integer status;

    public TaskWorker() {
        this.task = null;
        this.status = 0;
    }

    public synchronized void setTask(Task task) {
        this.task = task;
        this.status = 1;
    }

    public int getStatus() {
        return this.status;
    }

    public void run() {
        while(true) {
            synchronized (task) {
                try {
                if (task == null) {

                    // Wait for the task executor to assign a task.
                    task.wait();

                    // After receiving a task, set status to 1 (busy)
                    status = 1;

                    // Execute the given task.
                    task.run();

                    // Once task is completed, reset task to null for a new task to be assigned.
                    task = null;

                    // Set status to 0 (idle)
                    status = 0;

                }
                } catch (Exception e) {

                }
            }
        }
    }
}
