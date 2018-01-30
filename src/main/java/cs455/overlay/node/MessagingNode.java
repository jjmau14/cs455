package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
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
    private TCPServerThread tcpServer;
    private int[] nodes;

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
            new Thread(this.tcpServer = new TCPServerThread(0), "Messenger").start();

            // Register this node with the registry
            register(ip, port);

        } catch (Exception e) {
            throw e;
        }
    }

    private void register(String RegistryIP, int RegistryPort) throws Exception {
        try {
            Socket registerSocket = new Socket(RegistryIP, RegistryPort);
            TCPConnection conn = new TCPConnection(registerSocket);
            conn.init();
            OverlayNodeSendsRegistration ONSR = new OverlayNodeSendsRegistration(
                    InetAddress.getLocalHost().getAddress(),
                    this.tcpServer.getPort());

            conn.sendData(ONSR.pack());

        } catch (IOException ioe){
            System.out.println("[" + Thread.currentThread().getName() + "] Error registering node: " + ioe.getMessage());
            System.exit(1);
        }
    }

    public void onEvent(TCPConnection conn, Event e) throws Exception {
        switch(e.getType()){
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                RegistryReportsRegistrationStatus RRRS = (RegistryReportsRegistrationStatus)e;
                System.out.println(RRRS.getMessage());
                this.id = RRRS.getId();
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                RegistrySendsNodeManifest RSNM = (RegistrySendsNodeManifest)e;

                this.routingTable = RSNM.getRoutes();
                this.nodes = RSNM.getNodes();
                try {
                    setupOverlayConnections();
                    NodeReportsOverlaySetupStatus NROSS = new NodeReportsOverlaySetupStatus(this.id, "Overlay connections have been initialized.");
                    conn.sendData(NROSS.pack());
                } catch (Exception err){
                    System.out.println("Error creating overlay on node: " + err.getMessage());
                }
                break;
        }
    }

    private void setupOverlayConnections() throws Exception {
        for (int i = 0 ; i < routingTable.getTableSize() ; i++){
            TCPConnection conn = new TCPConnection(new Socket(routingTable.getRoute(i).ipToString(), routingTable.getRoute(i).getPort()));
            cache.addConnection(routingTable.getRoute(i).getGuid(), conn);
        }
        /*cache.doForAll((Integer i) -> {
            System.out.println(cache.getConnectionById(i).getSocket().getInetAddress().getHostAddress() + ":" + cache.getConnectionById(i).getSocket().getPort());
            return true;
        });*/
    }

}
