package edu.csu.jjmau14;

import edu.csu.jjmau14.registry.Registry;
import edu.csu.jjmau14.util.ControlMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }
    }

}
