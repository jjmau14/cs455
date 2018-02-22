package cs455.scaling.client;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

public class Client {

    private String serverHost;
    private int serverPort;
    private int messageRate;
    private SocketChannel client;
    private ByteBuffer buffer;
    private Selector selector;

    public Client(String serverHost, int serverPort, int messageRate) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }

    public void init() {
        try {
            this.selector = Selector.open();
            this.client = SocketChannel.open();
            this.client.configureBlocking(false);
            this.client.register(this.selector, SelectionKey.OP_CONNECT);
            this.client.connect(new InetSocketAddress(this.serverHost, this.serverPort));

            while(true){
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isConnectable()) {
                        this.connect(key);
                    }

                    if (key.isReadable()) {
                        this.read(key);
                    }

                    if (key.isWritable()) {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e){}
                        this.write(key);
                    }

                    keys.remove();
                }
            }
        } catch (Exception e){

        } finally {
            try {
                client.close();
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    }

    private void write(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(3);
        buffer = ByteBuffer.wrap(new byte[]{2,2,2});
        try {
            while (buffer.hasRemaining())
                channel.write(buffer);
        } catch (Exception e) {

        }
        buffer.clear();
        key.interestOps(SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(3);
        int read = 0;
        try {
            while (buffer.hasRemaining() && read != -1) {
                read = channel.read(buffer);
            }
        } catch (IOException e) {

        }

        if (read == -1) {
            return;
        }

        System.out.println("Received: " + Arrays.toString(buffer.array()));
        buffer.clear();
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void connect(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.finishConnect();
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 3){
            System.out.println("USAGE: java cs455.scaling.client.Client [Server Host] [Server Port] [Message Rate]");
        } else {
            Client client = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[1]));
            client.init();
        }
    }

}
