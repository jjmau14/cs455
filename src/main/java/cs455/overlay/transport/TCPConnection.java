package cs455.overlay.transport;

import java.io.IOException;
import java.net.Socket;

public class TCPConnection {

    private Socket socket = null;
    private TCPReceiver receiver;
    private TCPSender sender;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        sender = new TCPSender(this);
        receiver = new TCPReceiver(this);
    }

    public void init(){
        try {
            Thread senderThread = new Thread(sender, "Sender Thread");
            senderThread.start();

            Thread receiverThread = new Thread(receiver, "Receiver Thread");
            receiverThread.start();
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }
    }

    public void sendData(byte[] b){
        this.sender.send(b);
    }

    public Socket getSocket(){
        return this.socket;
    }

}
