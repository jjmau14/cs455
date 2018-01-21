package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection.TCPReceiver;
import cs455.overlay.transport.TCPConnection.TCPSender;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class MessagingNode extends Node {

    private static ServerSocket server;
    private static int id = -1;

    public static void main(String[] args) throws Exception {
        if (args.length != 2){
            System.out.println("USAGE: java cs455.overlay.node.MessagingNode [Registry Host] [Registry Port]");
            System.exit(1);
        }

        try {
            // Initialize server to get port to send to registry
            server = new ServerSocket(0);

            // Register this node with the registry
            register(args[0], Integer.parseInt(args[1]));

            // Accept all connections
            cycle();

        } finally {
            server.close();
        }
    }

    private static void register(String RegistryIP, int RegistryPort) {
        try (
                Socket registerSocket = new Socket(RegistryIP, RegistryPort)
        ){
            registerSocket.setKeepAlive(true);
            OverlayNodeSendsRegistration ONSR = new OverlayNodeSendsRegistration(
                    InetAddress.getLocalHost().getAddress(),
                    server.getLocalPort());

            System.out.println("Node requesting registration: " + Arrays.toString(ONSR.pack()));
            new TCPSender(registerSocket).sendData(ONSR.pack());
            byte[] data = new TCPReceiver(registerSocket).read();

            RegistryReportsRegistrationStatus RRRS = new RegistryReportsRegistrationStatus();
            RRRS.craft(data);

            id = RRRS.getId();
            System.out.println(RRRS.getMessage());

        } catch (IOException ioe){
            System.out.println("[" + Thread.currentThread().getName() + "] Error registering node: " + ioe.getMessage());
            System.exit(1);
        }
    }

    /**
     * @author: Josh Mau | 1/20/2018
     * initialize function creates a socket with the registry.
     * */
    private static void cycle() {
        try {

            while(true){
                Socket socket = server.accept();
            }

        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "]: Error in server thread: " + e.getMessage());
            System.exit(1);
        }
    }

}
