package cs455.scaling.task;

public class TaskWorker extends Thread {

    private Task task;
    private int status;

    public TaskWorker() {
        this.task = null;
        this.status = 0;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void run() {
        while(true) {
            synchronized (task) {
                if (task == null) {
                    try {
                        task.wait();
                    } catch (Exception e) {

                    }
                }
                task.run();
                task = null;
                
            }
        }
    }
}
