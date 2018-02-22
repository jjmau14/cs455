package cs455.scaling.server;

import cs455.scaling.task.Task;
import cs455.scaling.task.TaskPool;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;

public class Server {

    private int port;
    private int poolSize;
    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer buf;
    private TaskPool tasks;

    public Server(int port, int poolSize) {
        this.port = port;
        this.poolSize = poolSize;
        this.tasks = new TaskPool();
    }

    private void threadPool() {
        while(true) {
            Task t = this.tasks.pop();
            System.out.println("READ: " + Arrays.toString(t.getData()));
            SocketChannel channel = (SocketChannel) t.getKey().channel();
            ByteBuffer buffer = ByteBuffer.wrap(new byte[]{1, 2, 3});
            while (buffer.hasRemaining()) {
                try {
                    channel.write(buffer);
                } catch (Exception e) {
                }
            }
        }
    }

    public void init() {
        try {
            new Thread(() -> threadPool()).start();
            this.selector = Selector.open();
            this.server = ServerSocketChannel.open();
            this.server.socket().bind(new InetSocketAddress("localhost", port));
            this.server.configureBlocking(false);
            this.server.register(selector, SelectionKey.OP_ACCEPT);
            this.buf = ByteBuffer.allocate(3);

            System.out.println("Server listening on " + server.getLocalAddress());
            while(true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isAcceptable()) {
                        register(key);
                    }

                    if (key.isReadable()) {
                        addTask(key);
                    }

                    keys.remove();
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void register(SelectionKey key) {
        try {
            ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocket.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);

        } catch (Exception e) {
            ;
        }
    }

    private void addTask(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(3);
        int read = -1;
        try {
            while (buffer.hasRemaining()) {
                read = channel.read(buffer);
            }
        } catch (IOException e) {
            /* Abnormal termination */
            // Cancel the key and close the socket channel
        }
        // You may want to flip the buffer here
        if (read == -1) {
            try {
                System.out.println("Client terminated connection: " + ((SocketChannel) key.channel()).getRemoteAddress());
                key.channel().close();
            } catch (Exception e) {
            }
            return;
        }
        this.tasks.put(new Task(key, buffer.array()));
    }

    /*private void readAndReply(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(3);
        int read = -1;
        try {
            while (buffer.hasRemaining()) {
                read = channel.read(buffer);
            }
        } catch (IOException e) {
            / * Abnormal termination * /
            // Cancel the key and close the socket channel
        }
        // You may want to flip the buffer here
        if (read == -1) {
            try {
                System.out.println("Client terminated connection: " + ((SocketChannel) key.channel()).getRemoteAddress());
                key.channel().close();
            } catch (Exception e){}
            return;
        }
        try {
        System.out.println("Received: " + Arrays.toString(buffer.array()) + " from " + ((SocketChannel) key.channel()).getRemoteAddress());

        key.interestOps(SelectionKey.OP_WRITE);
        buffer = ByteBuffer.wrap(new byte[]{5,6,7});

            while(buffer.hasRemaining())
                channel.write(buffer);
        } catch (Exception e){}
        key.interestOps(SelectionKey.OP_READ);
    }*/


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

    public static void main(String[] args) {
        if (args.length != 2){
            System.out.println("USAGE: java cs455.scaling.server.Server [Port] [Thread Pool Size]");
            System.exit(1);
        } else {
            Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[0]));
            server.init();
        }
    }

}
