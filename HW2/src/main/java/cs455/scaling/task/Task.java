package cs455.scaling.task;

import java.nio.channels.SelectionKey;

public class Task {

    private byte[] data;
    private SelectionKey key;

    public Task(SelectionKey key, byte [] data) {
        this.key = key;
        this.data = data;
    }

    public byte[] getData() {
        return this.data;
    }

    public SelectionKey getKey() {
        return key;
    }
}
