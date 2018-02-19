package cs455.scaling.net;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Session {

    SelectionKey key;
    SocketChannel channel;
    ByteBuffer buf;

    public Session(SelectionKey key){
        this.key = key;
        channel = (SocketChannel) this.key.channel();
        buf = ByteBuffer.allocate(8);
    }

}
