package cs455.scaling.util;

public class Counter {

    private int count;

    public Counter() {
        this.count = 0;
    }

    public synchronized int increment() {
        this.count = count + 1;
        return this.count;
    }

    public synchronized int getCount() {
        return this.count;
    }

    public synchronized void reset() {
        this.count = 0;
    }
}
