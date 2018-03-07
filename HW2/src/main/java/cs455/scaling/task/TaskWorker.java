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
        synchronized (this.status) {
            this.status = 1;
            this.status.notify();
        }
    }

    public synchronized int getStatus() {
        synchronized (this.status) {
            return this.status;
        }
    }

    public void run() {
        while(true) {
            try {
                synchronized (status) {
                    while (status == 0) {
                        try {
                            status.wait();
                        } catch (Exception e){}
                    }
                    // Wait for the task executor to assign a task.

                    // After receiving a task, set status to 1 (busy)
                        status = 1;
                }

                // Execute the given task.
                //System.out.println("Runnign task");
                task.run();

                // Set status to 0 (idle)
                synchronized (status) {
                    status = 0;
                }

            } catch (Exception e) {

            }
        }
    }
}