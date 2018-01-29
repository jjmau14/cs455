package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

public class TCPSender implements Runnable {

    private Socket socket;
    private DataOutputStream dout;
    private static Queue<byte[]> queue;

    public TCPSender(Socket socket) throws IOException {
        this.queue = new PriorityQueue<>();
        this.socket = socket;
        this.dout = new DataOutputStream(this.socket.getOutputStream());
    }

    @Override
    public void run(){
        System.out.println("Sender Running");
        try {
            while(true) {

                synchronized (queue) {
                    while (queue.peek() == null) {
                        System.out.println("waiting");
                        queue.wait();
                    }

                    byte[] data = queue.poll();
                    System.out.println(Arrays.toString(data));
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
            this.queue.notify();
            System.out.println("Sending: " + Arrays.toString(queue.peek()));

        }
    }

}