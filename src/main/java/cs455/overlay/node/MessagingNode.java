package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnection.TCPReceiver;
import cs455.overlay.transport.TCPConnection.TCPSender;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class MessagingNode extends Node {

    private TCPServerThread server;
    private int id = -1;
    private RoutingTable routingTable;
    private EventFactory eventFactory;
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
        this.eventFactory = new EventFactory(this);
        try {
            // Initialize server to get port to send to registry

            // Register this node with the registry
            register(ip, port);

            // Accept all connections
            new Thread(new TCPServerThread(0), "Messenger").start();

        } catch (Exception e) {
            throw e;
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

    public void onEvent(TCPConnection conn, Event e){

    }

}
