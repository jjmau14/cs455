package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection.TCPReceiver;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Registry extends Node{

    public static void main(String[] args){
        if (args.length != 1){
            System.out.println("USAGE: java cs455.overlay.node.Registry [Port Number]");
            System.exit(1);
        }

        start(Integer.parseInt(args[0]));
    }

    private static void start(int port){
        new Thread(() -> serverThread(port), "Server Thread").start();
    }

    private static void serverThread(int port){
        try (
                ServerSocket ss = new ServerSocket(port)
        ){
            System.out.println("Registry running on " + InetAddress.getLocalHost().getHostAddress() + ":" + ss.getLocalPort() + "...");
            while(true){
                Socket socket = ss.accept();

                TCPReceiver r = new TCPReceiver(socket);
                byte[] data = r.read();
                System.out.println(Arrays.toString(data));
                switch (data[0]){
                    case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                        OverlayNodeSendsRegistration ONSR = new OverlayNodeSendsRegistration();
                        ONSR.craft(data);
                }
            }
        } catch (Exception e){
            System.out.println("[Registry - " + Thread.currentThread().getName() + "]: Error in server thread: " + e.getMessage());
            System.exit(1);
        }
    }

}
