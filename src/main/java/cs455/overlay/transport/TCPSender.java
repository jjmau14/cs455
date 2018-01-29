package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TCPSender implements AutoCloseable {

    private TCPConnection conn;
    private DataOutputStream dout;

    public TCPSender(TCPConnection conn) throws IOException {
        this.conn = conn;
        this.dout = new DataOutputStream(conn.getSocket().getOutputStream());
    }

    public void sendData(byte[] data) throws Exception {
        System.out.println("Sending: " + Arrays.toString(data));
        System.out.println(dout);
        try {
            int dataLength = data.length;
            dout.writeInt(dataLength);
            dout.write(data, 0, dataLength);
            System.out.println(dout.size());
            dout.flush();
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread().getName() + "] Error sending data: " + e.getMessage());
            return;
        }
    }

    public void close() throws IOException {
        if (!this.conn.getSocket().isClosed())
            this.conn.getSocket().close();
    }
}