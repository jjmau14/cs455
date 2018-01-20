package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection.TCPSender;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MessagingNode extends Node {

    private static int MessagerPort = 0;
    private static String RegistryIP = "";
    private static int RegistryPort = 0;

    public static void main(String[] args) throws Exception {
        if (args.length != 2){
            System.out.println("USAGE: java cs455.overlay.node.MessagingNode [Registry Host] [Registry Port]");
            System.exit(1);
        }

        RegistryIP = args[0];
        RegistryPort = Integer.parseInt(args[1]);
        new Thread(() -> start(), "Server").start();

    }

    private static int register() {
        try (
                Socket s = new Socket(RegistryIP, RegistryPort)
        ){
            OverlayNodeSendsRegistration register = new OverlayNodeSendsRegistration(InetAddress.getLocalHost().getAddress(), MessagerPort);
            new TCPSender(s).sendData(register.pack());

        } catch (IOException ioe){
            System.out.println("[" + Thread.currentThread().getName() + "] Error registering node: " + ioe.getMessage());
            System.exit(1);
        }
        return 0;
    }

    /**
     * @author: Josh Mau | 1/20/2018
     * initialize function creates a socket with the registry.
     * */
    private static void start() {
        try (
                ServerSocket ss = new ServerSocket(0)
        ){
            // Set port
            MessagerPort = ss.getLocalPort();
            System.out.println(MessagerPort);

            int id = register();
            System.out.println("Node registered successfully, id: " + id);

            while(true){
                Socket socket = ss.accept();
            }

        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "]: Error in server thread: " + e.getMessage());
            System.exit(1);
        }
    }

}
