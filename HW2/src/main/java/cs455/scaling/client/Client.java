package cs455.scaling.client;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Client {

    private String serverHost;
    private int serverPort;
    private int messageRate;
    private SocketChannel channel;
    private ByteBuffer buffer;
    private Selector selector;
    public final int BUFFER_SIZE = 8192;
    private HashList hashList;

    public Client(String serverHost, int serverPort, int messageRate) {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        this.hashList = new HashList();
    }

    public void init() {
        try {
            new Thread(() -> writer()).start();
            this.selector = Selector.open();
            this.channel = SocketChannel.open();
            this.channel.configureBlocking(false);
            this.channel.register(this.selector, SelectionKey.OP_CONNECT);
            this.channel.connect(new InetSocketAddress(this.serverHost, this.serverPort));

            while(true){
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isConnectable()) {
                        this.connect(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    }

                    keys.remove();
                }
            }
        } catch (Exception e){

        } finally {
            try {
                channel.close();
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    }

    private void writer() {
        boolean flag = true;
        while(true) {
            this.buffer.rewind();
            byte[] data = new byte[8192];
            Random r = new Random();
            for (int i = 0 ; i < 8192 ; i++) {
                data[i] = (byte)r.nextInt();
            }
            this.buffer = ByteBuffer.wrap(data);
            try {

                if (flag) {
                    flag = false;
                    SHA1FromBytes(buffer.array());
                }

                while (buffer.hasRemaining()){
                    int added = this.channel.write(buffer);
                    if (added == BUFFER_SIZE){
                        flag = true;
                    }

                }
                Thread.sleep(5000);
            } catch (Exception e) {

            }
        }
    }

    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(40);
        int read = 0;

        try {
            //while (buffer.hasRemaining() && read != -1) {
                read = channel.read(buffer);
                buffer.flip();
                byte[] data = new byte[buffer.limit()];
                for (int i = 0 ; i < data.length ; i++) {
                    data[i] = buffer.get();
                }
                System.out.println("Received: " + new String(data));
                System.out.println("Removed: " + this.hashList.removeIfPresent(new String(data)));
                //System.out.println(read);
            //}
        } catch (IOException e) {

        }

        if (read == -1) {
            System.out.println("Closed");
            return;
        }

    }

    private void connect(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.finishConnect();
            key.interestOps(SelectionKey.OP_READ);
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

    private class HashList {

        private LinkedList<String> list;

        public HashList() {
            this.list = new LinkedList<>();
        }

        public void add(String item) {
            synchronized (this.list) {
                System.out.println("Added: " + item);
                this.list.add(item);
                this.list.notify();
            }
        }

        public String removeIfPresent(String item) {
            String itemRemoved = null;
            synchronized (this.list) {
                if (list.contains(item)) {
                    itemRemoved = list.get(list.indexOf(item));
                    list.remove(item);
                }
            }
            return itemRemoved;
        }
    }

    private String SHA1FromBytes(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(data);
            BigInteger hashInt = new BigInteger(1, hash);
            String hashedString = hashInt.toString(16);
            System.out.println("HASHED: " + Arrays.toString(hashedString.getBytes()));
            this.hashList.add(hashedString);
            return hashedString;
        } catch (Exception  e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
