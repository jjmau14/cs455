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
import java.util.Random;
import java.util.Scanner;

public class Client {

    private String serverHost;
    private int serverPort;
    private int messageRate;
    private SocketChannel client;
    private ByteBuffer buf;

    public Client(String serverHost, int serverPort, int messageRate) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }

    public void init() {
        try {
            Selector selector = Selector.open();
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_CONNECT);
            channel.connect(new InetSocketAddress(this.serverHost, this.serverPort));
            while(true){
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()) {
                    SelectionKey key = keys.next();
                    System.out.println(key);
                    if (key.isWritable()) {
                        buf = ByteBuffer.wrap(new byte[] {1,2,3,4});
                        channel.write(buf);
                        buf.clear();
                        System.out.println("Sleep 2000");
                        Thread.sleep(1000);
                    }

                    if (key.isConnectable()) {
                        this.connect(key);
                    }

                    if (key.isReadable()) {
                        this.read(key);
                    }
                    keys.remove();
                }
            }
        } catch (Exception e){

        }

    }

    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8);
        int read = 0;
        try {
            while (buffer.hasRemaining() && read != -1) {
                read = channel.read(buffer);
            }
        } catch (IOException e) {
            /* Abnormal termination */
            // Cancel the key and close the socket channel
        }
        // You may want to flip the buffer here
        if (read == -1) {
        /* Connection was terminated by the client. */
            // Cancel the key and close the socket channel
            return;
        }
        buffer.clear();
        System.out.println("Received: " + read);
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
