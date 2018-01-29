import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.Queue;

public class main {

    private Queue<Byte> q = new PriorityQueue<Byte>();

    public static void main(String[] args) throws Exception {
        main m = new main();
        new Thread(() -> {
            try {
                m.server();
            } catch (Exception e){
                System.out.println("SERVER: " + e.getMessage());
            }
        }).start();
        new Thread(() -> {
            try {
                m.client();
            } catch (Exception e){
                System.out.println("CLIENT: " + e.getMessage());

            }
        }).start();
        synchronized (m.q) {
            m.q.add(new Byte((byte) 1));
            m.q.notify();
        }
        Thread.sleep(5000);
        synchronized (m.q) {
            m.q.add(new Byte((byte) 2));
            m.q.notify();
        }
    }

    private void server() throws Exception {
        ServerSocket server = new ServerSocket(5000);
        while(true){
            Socket s = server.accept();
            TCPConnection conn = new TCPConnection(s);
            serverWorker(conn);
        }
    }

    private void serverWorker(TCPConnection conn){
        new Thread(() -> {
            try {
                while(conn.getS() != null) {
                    DataInputStream din = new DataInputStream(conn.getS().getInputStream());
                    System.out.println(din.readByte());
                }
            } catch (Exception e){
                System.out.println("SERVER: " + e.getMessage());
            }
        }).start();
    }

    private void client() throws Exception {
        Socket s = new Socket("127.0.0.1", 5000);

        TCPConnection conn = new TCPConnection(s);
        clientWorker(conn);
    }

    private void clientWorker(TCPConnection conn){
        new Thread(() -> {
            try {
                DataOutputStream dout = new DataOutputStream(conn.getS().getOutputStream());
                while(true) {
                    synchronized (q) {
                        while (q.peek() == null) {
                            q.wait();
                        }
                        dout.writeByte(q.poll());
                        q.notify();
                    }
                }
            } catch (Exception e){
                System.out.println("CLIENT2: " + e.getMessage());
            }
        }).start();
    }

    private class TCPConnection{
        private Socket s;
        public TCPConnection(Socket s){
            this.s = s;
        }

        public Socket getS() {
            return s;
        }
    }

}
