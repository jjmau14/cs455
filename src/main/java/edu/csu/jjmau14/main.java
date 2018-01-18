package edu.csu.jjmau14;

import edu.csu.jjmau14.registry.Registry;
import edu.csu.jjmau14.util.ControlMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class main {

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            serve();
        }, "Server Thread").start();
        new Thread(() -> {
            client();
        }, "Client Thread").start();
    }

    public static void serve(){
        Registry r = new Registry();
    }

    public static void client() {
        try {
            Socket socket = new Socket("localhost", 5000);
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            dOut.write(ControlMessages.OVERLAY_NODE_SENDS_REGISTRATION);
            dOut.write(InetAddress.getLocalHost().getAddress().length);
            dOut.write(InetAddress.getLocalHost().getAddress());
            dOut.write(socket.getPort());
            socket.close();
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            dIn.readByte();
            System.out.println("Unique ID: " + dIn.readByte());
            int length = dIn.readByte();
            byte[] message = new byte[length];
            for (int i = 0 ; i < length ; i++){
                message[i] = dIn.readByte();
            }
            System.out.println(new String(message));
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }
    }

}
