package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable {

    private ServerSocket server;

    public TCPServerThread(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            System.out.println("Server running on " + InetAddress.getLocalHost().getHostAddress() + ":" + server.getLocalPort() + "...");
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        try {
            while(true){
                Socket socket = server.accept();
                TCPConnection conn = new TCPConnection(socket);
                conn.init();
            }
        } catch (IOException ioe){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + ioe.getMessage());
        } catch (Exception e){

        }
    }

    public int getPort(){
        return this.server.getLocalPort();
    }
    public String getHost(){
        try{
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e){
            ;
        }
        return null;
    }
    public byte[] getHostBytes(){
        return this.server.getInetAddress().getAddress();
    }
}
