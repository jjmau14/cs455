package cs455.scaling.task;

public class TaskWorker extends Thread {

    private Task task;
    private Boolean status;
    private int id;
    private TaskPool pool;

    public TaskWorker(int id, TaskPool pool) {
        this.id = id;
        this.pool = pool;
        this.task = new Task(null, null);
        this.status = new Boolean(false);
    }

    public void setTask(Task task) {
        synchronized (status) {
            status = true;
            this.task = task;
            status.notify();
        }
    }

    public void run() {
        while(true) {
            try {

                synchronized (status) {
                    while (!status)
                        status.wait();
                }

                System.out.println("Runnign task");

                // Execute task
                task.run();

                // Set status to 0 (idle)
                synchronized (status) {
                    this.status = false;
                }

                this.pool.notify(this.id);

            } catch (Exception e) {
                System.out.println("Error running task: " + e.getMessage());
            }
        }
    }
}