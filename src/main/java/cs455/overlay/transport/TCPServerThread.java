package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable {

    private ServerSocket server;

    public TCPServerThread(int port) throws IOException {
        this.server = new ServerSocket(port);
        System.out.println("Server running on " + InetAddress.getLocalHost().getHostAddress() + ":" + server.getLocalPort() + "...");

    }

    public void run() {
        try {
            while(true){
                Socket socket = server.accept();
                TCPConnection conn = new TCPConnection(socket);
            }
        } catch (IOException ioe){
            ;
        }
    }

    public int getPort(){
        return this.server.getLocalPort();
    }

}
