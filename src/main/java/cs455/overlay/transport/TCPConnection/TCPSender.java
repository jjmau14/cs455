package cs455.overlay.transport.TCPConnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender implements Runnable {

    private Socket socket;
    private DataOutputStream dout;
    private byte[] data;

    public TCPSender(Socket socket, byte[] data) throws IOException{
        this.socket = socket;
        this.data = data;
        this.dout = new DataOutputStream(socket.getOutputStream());
    }

    public void run(){
        try {
            int dataLength = data.length;
            dout.writeInt(dataLength);
            dout.write(data, 0, dataLength);
            dout.flush();
        } catch (Exception e){
            System.out.println("Error sending data: " + e.getMessage());
            return;
        }
    }

}
