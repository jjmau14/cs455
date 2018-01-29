package cs455.overlay.transport;

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
        this.din = new DataInputStream(this.socket.getInputStream());
    }

    @Override
    public void run(){
        System.out.println("Receiver running.");
        int dataLength;
        byte[] data = null;
        while(socket != null) {
            try {
                dataLength = din.readInt();
                System.out.println("Data length: " + dataLength);
                System.out.println(dataLength);
                data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
                System.out.println(Arrays.toString(data));
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
                System.out.println("Received: " + Arrays.toString(data) + " from " + this.socket);
                EventFactory ef = EventFactory.getInstance();
                ef.run(socket, data);
            }
        }
    }

}