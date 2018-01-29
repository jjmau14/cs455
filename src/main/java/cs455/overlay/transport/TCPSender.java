package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

public class TCPSender implements Runnable {

    private Socket socket;
    private DataOutputStream dout;
    private Queue<byte[]> queue;
    private TCPConnection conn;

    public TCPSender(TCPConnection conn) throws IOException {
        this.conn = conn;
        this.queue = new PriorityQueue<>();
        this.socket = conn.getSocket();
        this.dout = new DataOutputStream(this.socket.getOutputStream());
    }

    @Override
    public void run(){
        System.out.println("Sender ready to send data from me (" + socket.getInetAddress() + ":" + socket.getLocalPort() + ") to " + socket.getLocalAddress() + ":" + socket.getPort() + "");
        try {
            while(true) {

                synchronized (queue) {
                    while (queue.peek() == null) {
                        queue.wait();
                    }

                    byte[] data = queue.poll();
                    queue.notify();

                    try {
                        int dataLength = data.length;
                        dout.writeInt(dataLength);
                        dout.write(data, 0, dataLength);
                        dout.flush();
                    } catch (Exception e) {
                        System.out.println("[" + Thread.currentThread().getName() + "] Error sending data: " + e.getMessage());
                    }

                }
            }
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }
    }

    public void send(byte[] data) {
        synchronized (queue){
            this.queue.add(data);
            queue.notify();
        }
    }

}