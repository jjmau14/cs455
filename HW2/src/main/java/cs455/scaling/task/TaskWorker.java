package cs455.scaling.task;

public class TaskWorker extends Thread {

    private Task task;
    private Integer status;

    public TaskWorker() {
        this.task = null;
        this.status = 0;
    }

    public void setTask(Task task) {
        this.task = task;
        synchronized (this.status) {
            this.status = 1;
            this.status.notify();
        }
    }

    public int getStatus() {
        synchronized (this.status) {
            return this.status;
        }
    }

    public void run() {
        while(true) {
            try {
                synchronized (this.status) {
                    while (this.status == 0) {
                        try {
                            this.status.wait();
                        } catch (Exception e){}
                    }
                }

                task.run();

                // Set status to 0 (idle)
                synchronized (this.status) {
                    this.status = 0;
                }

            } catch (Exception e) {

            }
        }
    }
}