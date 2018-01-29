package cs455.overlay.transport;

import cs455.overlay.routing.Route;
import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

public class TCPConnection {

    private Socket socket;
    private Queue<byte[]> queue;
    private int id;
    private Route routingData;

    public TCPConnection(Socket socket){
        this.queue = new PriorityQueue<>();
        this.socket = socket;
        this.routingData = new Route(socket.getInetAddress().getAddress(), socket.getPort(), -1);
        this.id = -1;
        init();
    }

    private void init(){
        Thread receiver = new Thread(() -> {
            try (
                    TCPReceiver rec = new TCPReceiver(this)
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
                    TCPSender send = new TCPSender(this)
            ){
                while(true){
                    synchronized (queue) {
                        while (queue.peek() == null) {
                            System.out.println("waiting");
                            wait();
                        }

                        System.out.println("OK:" + Arrays.toString(queue.peek()));
                        send.sendData(queue.poll());

                        queue.notify();
                    }
                }
            } catch (Exception e){
                ;
            }
        });
        sender.start();
    }

    public void sendData(byte[] b){
        synchronized (queue) {
            this.queue.add(b);
            queue.notify();
        }
    }

    public Socket getSocket(){
        return this.socket;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setGuid(int guid){
        this.routingData.setGuid(guid);
    }

    public class TCPReceiver implements AutoCloseable{

        private TCPConnection conn;
        private DataInputStream din;

        public TCPReceiver(TCPConnection conn) throws IOException {
            this.conn = conn;
            this.din = new DataInputStream(this.conn.getSocket().getInputStream());
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
            EventFactory.getInstance().run(conn, data);

        }

        public void close() throws IOException {
            if (!this.conn.getSocket().isClosed())
                this.conn.getSocket().close();
        }
    }

    public class TCPSender implements AutoCloseable {

        private TCPConnection conn;
        private DataOutputStream dout;

        public TCPSender(TCPConnection conn) throws IOException {
            this.conn = conn;
            this.dout = new DataOutputStream(conn.getSocket().getOutputStream());
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
            if (!this.conn.getSocket().isClosed())
                this.conn.getSocket().close();
        }
    }

}
