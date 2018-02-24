package cs455.scaling.server;

import cs455.scaling.task.Task;
import cs455.scaling.task.TaskPool;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.Iterator;

public class Server {

    private int port;
    private int poolSize;
    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer buffer;
    private TaskPool tasks;
    public final int BUFFER_SIZE = 8192;

    public Server(int port, int poolSize) {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.port = port;
        this.poolSize = poolSize;
    }

    public void init() {
        try {
            this.tasks = new TaskPool(8);

            this.selector = Selector.open();
            this.server = ServerSocketChannel.open();
            this.server.socket().bind(new InetSocketAddress("localhost", port));
            this.server.configureBlocking(false);
            this.server.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server listening on " + server.getLocalAddress());
            while(true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isAcceptable()) {
                        register(key);
                    } else if (key.isReadable()) {
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
        int read = 0;
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        SocketChannel channel = (SocketChannel)key.channel();
        try {
            read = channel.read(buffer);
            if (read > 0){
                this.tasks.addTask(new Task(key, buffer));
            }
        } catch (Exception e) {

        }
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
