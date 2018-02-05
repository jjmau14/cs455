package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MessagingNode extends Node {

    private int id = -1;
    private RoutingTable routingTable;
    private EventFactory eventFactory;
    private TCPConnectionsCache cache;
    private TCPServerThread tcpServer;
    private int[] nodes;
    private Long dataTotal = 0l;
    private Integer packetsReceived = 0;
    private Integer packetsSent = 0;
    private Integer packetsForwarded = 0;

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
            Thread server = new Thread(this.tcpServer = new TCPServerThread(0), "Messenger");
            server.start();
            System.out.println("TCP SERVER: " + this.tcpServer.getPort());
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
            conn.exitOnClose();
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
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                RegistryRequestsTaskInitiate RRTI = (RegistryRequestsTaskInitiate)e;
                new Thread(() -> {
                    initDataStream(RRTI.getNumDataPackets());
                }).start();
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                OverlayNodeSendsData ONSD = (OverlayNodeSendsData)e;
                //System.out.println("Received " + ONSD.getPayload() + " from " + ONSD.getSourceId() + " en route to " + ONSD.getDestinationId() + ": " + Arrays.toString(ONSD.pack()) );
                if (ONSD.getDestinationId() == this.id){
                    synchronized (this.dataTotal){
                        this.dataTotal += ONSD.getPayload();
                    }
                    synchronized (this.packetsReceived){
                        this.packetsReceived += 1;
                    }
                } else {
                    synchronized (this.packetsForwarded){
                        this.packetsForwarded += 1;
                    }
                    ONSD.addTrace(this.id);
                    //System.out.println("Forwarding packet to " + ONSD.getDestinationId() + ": " + Arrays.toString(ONSD.pack()));
                    this.cache.getNearestId(ONSD.getDestinationId()).sendData(ONSD.pack());
                }
                break;
        }
    }

    private void setupOverlayConnections() throws Exception {

        for (int i = 0 ; i < routingTable.getTableSize() ; i++){
            try {
                TCPConnection conn = new TCPConnection(new Socket(routingTable.getRoute(i).ipToString(), routingTable.getRoute(i).getPort()));
                cache.addConnection(routingTable.getRoute(i).getGuid(), conn);
            } catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void initDataStream(int numDataPackets) {
        Random randomId = new Random();
        Random randomInt = new Random();
        int nodeId;
        OverlayNodeSendsData ONSD;
        for (int i = 0 ; i < numDataPackets ; i++){
            while ((nodeId = randomId.nextInt(this.nodes.length)) == this.id);
            int payload = randomInt.nextInt() - 2147483647 - 1;
            //System.out.println(this.id + " sending " + payload + " to " + nodeId);
            ONSD = new OverlayNodeSendsData(nodeId, this.id, payload, new int[0]);

            try {
                this.cache.getNearestId(nodeId).sendData(ONSD.pack());
                synchronized (this.packetsSent){
                    this.packetsSent += 1;
                }
            } catch (Exception e){
                System.out.println("[" + Thread.currentThread().getName() + "] Error sending datagram: " + e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(10*1000);
        }catch(Exception e){}
        System.out.println("Total Sent: " + this.packetsSent);
        System.out.println("Total Receiver: " + this.packetsReceived);
        System.out.println("Total Forwarded: " + this.packetsForwarded);
    }

}