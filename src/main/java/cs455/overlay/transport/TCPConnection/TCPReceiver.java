package cs455.overlay.transport.TCPConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiver {

    private Socket socket;
    private DataInputStream din;

    public TCPReceiver(Socket socket) throws IOException {
        this.socket = socket;
        din = new DataInputStream(socket.getInputStream());
    }

    public byte[] read(){
        int dataLength;
        byte[] data = null;
        while (socket != null) {
            try {
                dataLength = din.readInt();
                data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
            } catch (SocketException se) {
                System.out.println("SocketException: " + se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println("IOException: " + ioe.getMessage());
                break;
            }
        }
        return data;
    }

}
