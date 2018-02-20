package cs455.scaling.client;

import com.sun.org.apache.bcel.internal.generic.Select;

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
                        System.out.println("Sleep 2000");
                        Thread.sleep(100);
                    }

                    if (key.isConnectable()) {
                        this.connect(key);
                    }
                    keys.remove();
                }
            }
        } catch (Exception e){

        }

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

    private String sendMessage(String message) {
        String response = null;
        try {
            Random r = new Random();
            byte[] b = new byte[1000];
            for (int i = 0 ; i < 1000 ; i++){
                b[i] = (byte)r.nextInt();
            }
            buf = ByteBuffer.wrap(b);
            client.write(buf);
            buf.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return response;
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
