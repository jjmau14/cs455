package cs455.scaling.task;

public class TaskWorker extends Thread {

    private Task task;
    private Integer status;

    public TaskWorker() {
        this.task = new Task(null, null);
        this.status = new Integer(0);
    }

    public int setTask(Task task) {
        int temp = 0;
        try {
            synchronized (this.status) {
                // If the thread is idle (status == 0) assign this thread a task
                if (this.status == 0) {
                    this.task = task;
                    this.status = 1;
                    temp = 1;
                } else {
                    temp = -1;
                }
                status.notify();
            }
        } catch (Exception e) {
            System.out.println("Error setting task: " + e.getMessage());
        }
        return temp;
    }

    public void run() {
        while(true) {
            try {

                // Wait for this status to be 1 indicating task should be ran
                // and this thread can proceed.
                synchronized (this.status) {
                    while (this.status == 0) {
                        this.status.wait();
                    }
                }

                System.out.println("Runnign task");

                // Execute task
                task.run();

                // Reset task
                task = null;

                // Set status to 0 (idle)
                synchronized (this.status) {
                    this.status = 0;
                }

            } catch (Exception e) {
                System.out.println("Error running task: " + e.getMessage());
            }
        }
    }
}