package cs455.scaling;

import java.util.Iterator;

public class Counter {

    private Integer count;

    public Counter() {
        this.count = 0;
    }

    public synchronized Integer getCount() {
        int temp = count;
        count = 0;
        return temp;
    }

    public synchronized void increment() {
        count += 1;
    }

    public synchronized void reset() {
        count = 0;
    }

}
