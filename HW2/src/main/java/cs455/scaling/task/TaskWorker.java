package cs455.scaling.task;

public class TaskWorker extends Thread {

    private Task task;
    private Integer status;

    public TaskWorker() {
        this.task = null;
        this.status = 0;
    }

    public int setTask(Task task) {
        int temp = 0;
        synchronized (this.status) {
            if (this.status == 0) {
                this.task = task;
                this.status = 1;
                this.status.notify();
                temp = 1;
            } else {
                temp = -1;
            }
        }
        return temp;
    }

    public void run() {
        while(true) {
            try {
                while (status == 0) {
                    try {
                        status.wait();
                    } catch (Exception e){}
                }
                // Wait for the task executor to assign a task.

                // After receiving a task, set status to 1 (busy)
                synchronized (status) {
                    status = 1;
                }

                // Execute the given task.
                //System.out.println("Running task");
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