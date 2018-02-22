package cs455.scaling.task;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Task {

    SelectionKey key;
    SocketChannel channel;
    ByteBuffer buffer;

    public Task(SelectionKey key) {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
        this.buffer = ByteBuffer.allocate(3);
    }

    public void write() {
        try {
            while (this.buffer.hasRemaining())
                this.channel.write(this.buffer);
            buffer.clear();
        } catch (Exception e) {

        }
    }

    public byte[] read() {
        try {
            int read = -1;
            while (this.buffer.hasRemaining())
                read = this.channel.read(this.buffer);
        } catch (Exception e) {

        }
        this.buffer.clear();
        return this.buffer.array();
    }

}
