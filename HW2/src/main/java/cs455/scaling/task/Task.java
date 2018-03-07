package cs455.scaling.task;

import cs455.scaling.Counter;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;

public class Task implements Runnable {

    private SelectionKey key;
    public ByteBuffer buffer;

    public Task(SelectionKey key, ByteBuffer buffer) {
        this.key = key;
        this.buffer = buffer;
    }

    public void run() {
        byte[] data = buffer.array();
        SocketChannel channel = (SocketChannel) this.key.channel();
        ByteBuffer buf = ByteBuffer.wrap(this.SHA1FromBytes(data).getBytes());

        while (buf.hasRemaining()) {
            try {
                channel.write(buf);
            } catch (Exception e) {}
        }

    }

    private String SHA1FromBytes(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(data);
            BigInteger hashInt = new BigInteger(1, hash);
            return hashInt.toString(16);
        } catch (Exception  e) {
            ;
        }
        return null;
    }

}
