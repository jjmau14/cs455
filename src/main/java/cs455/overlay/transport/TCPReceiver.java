package cs455.overlay.transport;

import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

public class TCPReceiver {

    private TCPConnection conn;
    private DataInputStream din;

    public TCPReceiver(TCPConnection conn) throws IOException {
        this.conn = conn;
        this.din = new DataInputStream(this.conn.getSocket().getInputStream());
    }

    public void read() {
        int dataLength;
        byte[] data = null;
        while(conn.getSocket() != null) {
            try {
                dataLength = din.readInt();
                System.out.println(dataLength);
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
                System.out.println("Received: " + Arrays.toString(data));
                EventFactory.getInstance().run(conn, data);
            }
        }

    }

}