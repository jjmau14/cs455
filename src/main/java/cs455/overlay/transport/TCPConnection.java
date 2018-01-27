package cs455.overlay.transport;

import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;

public class TCPConnection {

    private Socket socket;
    private Queue<byte[]> queue;
    private int id;

    public TCPConnection(Socket socket){
        this.socket = socket;
        this.id = -1;
        init();
    }

    private void init(){
        Thread receiver = new Thread(() -> {
            try (
                    TCPReceiver rec = new TCPReceiver(this.socket)
            ){
                while(true){
                    rec.read();
                }
            } catch (Exception e){
                ;
            }
        });
        receiver.start();

        Thread sender = new Thread(() -> {
            try (
                    TCPSender send = new TCPSender(this.socket)
            ){
                while(true){
                    if (queue.peek() == null)
                        wait();
                    send.sendData(queue.poll());
                }
            } catch (Exception e){
                ;
            }
        });
        sender.start();
    }

    public void sendData(byte[] b){
        this.queue.add(b);
    }

    public void setId(int id){
        this.id = id;
    }

    public class TCPReceiver implements AutoCloseable{

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

        public void close() throws IOException {
            if (!this.socket.isClosed())
                this.socket.close();
        }
    }

    public class TCPSender implements AutoCloseable {

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

        public void close() throws IOException {
            if (!this.socket.isClosed())
                this.socket.close();
        }
    }

}
