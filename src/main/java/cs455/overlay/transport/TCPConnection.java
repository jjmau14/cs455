package cs455.overlay.transport;

import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPConnection {

    private Socket socket;

    public TCPConnection(Socket socket){
        this.socket = socket;
    }

    public class TCPReceiver {

        private Socket socket;
        private DataInputStream din;

        public TCPReceiver(Socket socket) throws IOException {
            this.socket = socket;
            this.din = new DataInputStream(socket.getInputStream());
        }

        public void read() {
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

    public class TCPSender {

        private Socket socket;
        private DataOutputStream dout;

        public TCPSender(Socket socket) throws IOException {
            this.socket = socket;
            this.dout = new DataOutputStream(socket.getOutputStream());
        }

        public void sendData(byte[] data) throws Exception {
            try {
                int dataLength = data.length;
                dout.writeInt(dataLength);
                dout.write(data, 0, dataLength);
                dout.flush();
            } catch (Exception e) {
                System.out.println("Error sending data: " + e.getMessage());
                return;
            }
        }
    }

}
