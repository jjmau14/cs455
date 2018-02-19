package cs455.scaling.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    private int port;
    private int poolSize;
    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer buf;

    public Server(int port, int poolSize) {
        this.port = port;
        this.poolSize = poolSize;
    }

    public void init() {
        try {
            this.selector = Selector.open();
            this.server = ServerSocketChannel.open();
            this.server.bind(new InetSocketAddress("localhost", port));
            this.server.configureBlocking(false);
            this.server.register(selector, SelectionKey.OP_ACCEPT);
            this.buf = ByteBuffer.allocate(256);

            System.out.println("Server listening on " + server.getLocalAddress());

            while(true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isAcceptable()) {
                        register(selector, server);
                    }

                    if (key.isReadable()) {
                        readAndReply(buf, key);
                    }

                    System.out.println("KEY: " + key.interestOps());
                    keys.remove();
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void register(Selector selector, ServerSocketChannel server) {
        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            ;
        }
    }

    private static void readAndReply(ByteBuffer buf, SelectionKey key) {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            client.read(buf);
            System.out.println("Received: " + new String(buf.array()));
            buf.clear();
            buf = ByteBuffer.wrap("hello there".getBytes());
            client.write(buf);
            buf.clear();
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
