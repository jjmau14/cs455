package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TCPSender {

    private TCPConnection conn;
    private DataOutputStream dout;

    public TCPSender(TCPConnection conn) throws IOException {
        this.conn = conn;
        this.dout = new DataOutputStream(conn.getSocket().getOutputStream());
    }

    public void sendData(byte[] data) throws Exception {
        try {
            int dataLength = data.length;
            System.out.println(dataLength);
            dout.writeInt(dataLength);
            dout.write(data, 0, dataLength);
            dout.flush();
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread().getName() + "] Error sending data: " + e.getMessage());
            return;
        }
    }

}