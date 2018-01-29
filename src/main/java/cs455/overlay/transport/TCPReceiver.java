package cs455.overlay.transport;

import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class TCPReceiver implements Runnable {

    private TCPConnection conn;
    private Socket socket;
    private DataInputStream din;

    public TCPReceiver(TCPConnection conn) throws IOException {
        this.socket = conn.getSocket();
        this.conn = conn;
        this.din = new DataInputStream(this.socket.getInputStream());
    }

    @Override
    public void run(){
        System.out.println("Receiver listening for data from " + socket.getInetAddress() + ":" + socket.getPort() + " to me(" + socket.getLocalAddress() + ":" + socket.getLocalPort() + ")");
        int dataLength;
        byte[] data = null;
        while(socket != null) {
            try {
                dataLength = din.readInt();
                data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
            } catch (SocketException se) {
                System.out.println("[" + Thread.currentThread().getName() + "] SocketException: " + se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println("[" + Thread.currentThread().getName() + "] IOException: " + ioe.getMessage());
                break;
            } catch (Exception e) {
                System.out.println("[" + Thread.currentThread().getName() + "] Exception: " + e.getMessage());
                break;
            }
            if (data != null) {
                EventFactory ef = EventFactory.getInstance();
                ef.run(conn, data);
            }
        }
    }

}