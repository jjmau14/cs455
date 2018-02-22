package cs455.scaling.server;

import java.io.IOException;
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

    public Server(int port, int poolSize) {
        this.port = port;
        this.poolSize = poolSize;
    }

    public void init() {
        try {
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
                        register(selector, key);
                    }

                    if (key.isReadable()) {
                        readAndReply(key);
                    }

                    keys.remove();
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void register(Selector selector, SelectionKey key) {
        try {
            ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocket.accept();
            System.out.println("Accepting incoming connection.");
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_WRITE);
            ByteBuffer buf = ByteBuffer.wrap(new byte[]{10,10,10});
            while (buf.hasRemaining())
                channel.write(buf);
            channel.register(selector, SelectionKey.OP_READ);

        } catch (Exception e) {
            ;
        }
    }

    private static void readAndReply(SelectionKey key) {
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
            } catch (Exception e){}
            return;
        }
        System.out.println("Received: " + read);

        key.interestOps(SelectionKey.OP_WRITE);
        buffer = ByteBuffer.wrap(new byte[]{5,6,7});
        try {
            while(buffer.hasRemaining())
                channel.write(buffer);
        } catch (Exception e){}
        key.interestOps(SelectionKey.OP_READ);
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
