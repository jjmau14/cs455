package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection.TCPReceiver;
import cs455.overlay.transport.TCPConnection.TCPSender;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class MessagingNode extends Node {

    private ServerSocket server;
    private int id = -1;
    private RoutingTable routingTable;
    private TCPConnectionsCache cache;

    public static void main(String[] args) throws Exception {
        if (args.length != 2){
            System.out.println("USAGE: java cs455.overlay.node.MessagingNode [Registry Host] [Registry Port]");
            System.exit(1);
        }
        MessagingNode node = new MessagingNode(args[0], Integer.parseInt(args[1]));

    }

    public MessagingNode(String ip, int port) throws Exception {
        this.cache = new TCPConnectionsCache();
        try {
            // Initialize server to get port to send to registry
            server = new ServerSocket(0);

            // Register this node with the registry
            register(ip, port);

            // Accept all connections
            cycle();

        } finally {
            server.close();
        }
    }

    private void register(String RegistryIP, int RegistryPort) throws Exception {
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
            System.out.println("ID: " + id + ". " + RRRS.getMessage());

            byte[] data2 = new TCPReceiver(registerSocket).read();
            System.out.println(Arrays.toString(data2));
            RegistrySendsNodeManifest RSNM = new RegistrySendsNodeManifest();
            RSNM.craft(data2);
            this.routingTable = RSNM.getRoutes();
            for (int i = 0 ; i < routingTable.getTableSize() ; i++){
                Socket s = new Socket(routingTable.getRoute(i).ipToString(), routingTable.getRoute(i).getPort());
                cache.addConnection(routingTable.getRoute(i).getGuid(), s);
            }
            System.out.println("Successfully configured overlay.");
            cache.doForAll((Integer i) -> {
                System.out.println(cache.getConnectionById(i).getInetAddress().getHostAddress() + ":" + cache.getConnectionById(i).getPort());
                return true;
            });
        } catch (IOException ioe){
            System.out.println("[" + Thread.currentThread().getName() + "] Error registering node: " + ioe.getMessage());
            System.exit(1);
        }
    }

    /**
     * @author: Josh Mau | 1/20/2018
     * initialize function creates a socket with the registry.
     * */
    private void cycle() {
        try {

            while(true){
                Socket socket = server.accept();

                byte[] data = new TCPReceiver(socket).read();

                switch(data[0]){
                    case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                        RegistrySendsNodeManifest RSNM = new RegistrySendsNodeManifest();
                        RSNM.craft(data);
                        System.out.println(RSNM.getRoutes().toString());
                }
            }

        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "]: Error in server thread: " + e.getMessage());
            System.exit(1);
        }
    }

}
