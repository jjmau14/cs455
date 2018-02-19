package cs455.scaling.client;

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
    private ByteBuffer buf;

    public Client(String serverHost, int serverPort, int messageRate) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }

    public void init() {
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 5000));
            buf = ByteBuffer.allocate(256);

            System.out.println("Response: " + sendMessage("Hello"));
        } catch (Exception e){

        }

    }

    private String sendMessage(String message) {
        buf = ByteBuffer.wrap(message.getBytes());
        String response = null;
        try {
            client.write(buf);
            buf.clear();
            client.read(buf);
            response = new String(buf.array());
            System.out.println(Arrays.toString(buf.array()));
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
