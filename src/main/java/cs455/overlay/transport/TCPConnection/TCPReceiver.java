package cs455.overlay.transport.TCPConnection;

import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class TCPReceiver implements Runnable {

    private Socket socket;
    private DataInputStream din;

    public TCPReceiver(Socket socket) throws IOException {
        this.socket = socket;
        din = new DataInputStream(socket.getInputStream());
    }

    public void run(){
        int dataLength;
        byte[] data = null;
        try {
            dataLength = din.readInt();
            data = new byte[dataLength];
            din.readFully(data, 0, dataLength);
        } catch (SocketException se) {
            System.out.println("SocketException: " + se.getMessage());
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        EventFactory.getInstance().run(data);
    }
}
