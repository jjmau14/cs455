package cs455.overlay.transport;

import cs455.overlay.transport.TCPConnection.TCPReceiver;
import cs455.overlay.transport.TCPConnection.TCPSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable {

    private ServerSocket server;

    public TCPServerThread(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    public void run() {
        try {
            while(true){
                Socket socket = server.accept();
                new Thread(new TCPReceiver(socket)).start();
            }
        } catch (IOException ioe){
            ;
        }
    }

    public int getPort(){
        return this.server.getLocalPort();
    }

}
