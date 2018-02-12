package cs455.overlay.transport;

import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class TCPConnection {

    private Socket socket = null;
    private boolean EXIT_ON_CLOSE = false;
    private PriorityQueue<byte[]> outQueue;
    private PriorityQueue<byte[]> inQueue;
    private DataInputStream din;
    private DataOutputStream dout;
    private Thread senderThread;
    private Thread receiverThread;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.din = new DataInputStream(this.socket.getInputStream());
        this.dout = new DataOutputStream(this.socket.getOutputStream());
    }

    public void init(){
        try {
            this.outQueue = new PriorityQueue<>(new Comparator<byte[]>() {
                @Override
                public int compare(byte[] o1, byte[] o2) {
                    return 0;
                }
            });
            this.inQueue = new PriorityQueue<>(new Comparator<byte[]>() {
                @Override
                public int compare(byte[] o1, byte[] o2) {
                    return 0;
                }
            });
            Thread receiverThread = new Thread(() -> receiver(), "Receiver Thread");
            receiverThread.start();
            Thread senderThread = new Thread(() -> new Sender(), "Sender Thread");
            senderThread.start();
            Thread receiverWorker = new Thread(() -> receiverWorker(), "Receiver Worker");
            receiverWorker.start();
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }
    }

    private class Sender{
        public Sender(){
            try {
                while (true) {
                    byte[] data = null;
                    synchronized (outQueue) {
                        if (outQueue.peek() == null)
                            outQueue.wait();
                        data = outQueue.poll();
                    }
                    int dataLength = data.length;
                    dout.writeInt(dataLength);
                    dout.write(data, 0, dataLength);
                    dout.flush();
                }
            } catch (Exception e){

            }
        }
    }

    public void sendData(byte[] data){
        synchronized (this.outQueue){
            this.outQueue.add(data);
            this.outQueue.notify();
        }
    }

    private void receiver(){
        //System.out.println("Receiver listening for data from " + socket.getInetAddress() + ":" + socket.getPort() + " to me(" + socket.getLocalAddress() + ":" + socket.getLocalPort() + ")");
        int dataLength;
        byte[] data = null;
        while(socket != null) {
            try {
                dataLength = din.readInt();
                data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
            } catch (SocketException se) {
                System.out.println("[" + Thread.currentThread().getName() + "] SocketException: " + se.getMessage());
                //se.printStackTrace();
                break;
            } catch (IOException ioe) {
                System.out.println("[" + Thread.currentThread().getName() + "] A Node has exited the overlay");
                //ioe.printStackTrace();
                break;
            } catch (Exception e) {
                System.out.println("[" + Thread.currentThread().getName() + "] Exception: " + e.getMessage());
                //e.printStackTrace();
                break;
            }
            if (data != null ) {
                synchronized (this.inQueue){
                    this.inQueue.add(data);
                    this.inQueue.notify();
                }
            }
        }
        //System.out.println("Socket closed.");
        if (this.EXIT_ON_CLOSE) {
            System.out.println("Connection with the registry failed. System exiting...");
            System.exit(1);
        }
    }

    public void receiverWorker(){
        while(true) {
            try {
                byte[] data = null;
                synchronized (this.inQueue) {
                    if (this.inQueue.peek() == null) {
                        this.inQueue.wait();
                    }

                    data = this.inQueue.poll();
                    this.inQueue.notify();
                }

                EventFactory ef = EventFactory.getInstance();
                //System.out.println("[" + Thread.currentThread().getName() + "] Received array: " + Arrays.toString(data));
                ef.run(this, data);
            } catch (Exception e) {

            }
        }
    }

    public Socket getSocket(){
        return this.socket;
    }

    public void exitOnClose(){
        this.EXIT_ON_CLOSE = true;
    }
}
