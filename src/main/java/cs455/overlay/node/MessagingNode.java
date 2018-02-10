package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.CommandParser;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class MessagingNode extends Node {

    private int id = -1;
    private RoutingTable routingTable;
    private EventFactory eventFactory;
    private TCPConnectionsCache cache;
    private TCPServerThread tcpServer;
    private int[] nodes;
    private TCPConnection registryConnection;
    private OverlayNodeReportsTrafficSummary ONRTS;

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
        this.ONRTS = new OverlayNodeReportsTrafficSummary();
        new Thread(() -> new CommandParser().messengerParser(this), "Command Parser").start();
        try {
            // Initialize server to get port to send to registry
            Thread server = new Thread(this.tcpServer = new TCPServerThread(0), "Messenger");
            server.start();

            // Register this node with the registry
            register(ip, port);

        } catch (Exception e) {
            throw e;
        }
    }

    private void register(String RegistryIP, int RegistryPort) throws Exception {
        try {
            Socket registerSocket = new Socket(RegistryIP, RegistryPort);
            this.registryConnection = new TCPConnection(registerSocket);
            this.registryConnection.exitOnClose();
            this.registryConnection.init();
            OverlayNodeSendsRegistration ONSR = new OverlayNodeSendsRegistration(
                    InetAddress.getLocalHost().getAddress(),
                    this.tcpServer.getPort());

            this.registryConnection.sendData(ONSR.pack());

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
                this.ONRTS.setId(this.id);
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
                this.ONRTS.reset();
                RegistryRequestsTaskInitiate RRTI = (RegistryRequestsTaskInitiate)e;
                System.out.println("New task requested for " + RRTI.getNumDataPackets() + " messages.");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initDataStream(RRTI.getNumDataPackets());
                    }
                }).start();
                break;


            case Protocol.OVERLAY_NODE_SENDS_DATA:
                OverlayNodeSendsData ONSD = (OverlayNodeSendsData)e;
                if (ONSD.getDestinationId() == this.id){
                    synchronized (this.ONRTS){
                        this.ONRTS.addSumReceived(ONSD.getPayload());
                        this.ONRTS.addPacketsReceived(1);
                    }
                } else {
                    synchronized (this.ONRTS){
                        this.ONRTS.addPacketsRelayed(1);
                    }
                    ONSD.addTrace(this.id);
                    this.cache.getNearestId(ONSD.getDestinationId()).sendData(ONSD.pack());
                }
                break;


            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                int packetsReceived = 0;
                do {
                    synchronized (ONRTS) {
                        packetsReceived = this.ONRTS.getPacketsReceived();
                    }
                    Thread.sleep(500);
                } while (packetsReceived != ONRTS.getPacketsReceived());
                synchronized (this.ONRTS) {
                    this.registryConnection.sendData(this.ONRTS.pack());
                }
                break;

            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                RegistryReportsDeregistrationStatus RRDS = (RegistryReportsDeregistrationStatus)e;
                if (RRDS.getStatus() == 1){
                    System.out.println("Overlay exit succeeded. Terminating program...");
                    System.exit(0);
                } else {
                    System.out.println("Error exiting overlay");
                }
                break;
        }
    }

    private void setupOverlayConnections() throws Exception {

        for (int i = 0 ; i < routingTable.getTableSize() ; i++){
            try {
                TCPConnection conn = new TCPConnection(new Socket(routingTable.getRoute(i).ipToString(), routingTable.getRoute(i).getPort()));
                conn.init();
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
            ONSD = new OverlayNodeSendsData(nodeId, this.id, payload, new int[0]);
            try {
                this.cache.getNearestId(nodeId).sendData(ONSD.pack());
                synchronized (this.ONRTS){
                    this.ONRTS.addSumSent(payload);
                    this.ONRTS.addPacketsSent(1);
                }
            } catch (Exception e){
                System.out.println("[" + Thread.currentThread().getName() + "] Error sending datagram: " + e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            this.registryConnection.sendData(new OverlayNodeReportsTaskFinished(
                    this.tcpServer.getHostBytes(),
                    this.tcpServer.getPort(),
                    this.id).pack());
            System.out.println("Sending Task Finished");
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error sending task finished: " + e.getMessage());
        }
        System.out.println("Done sending data.");
    }

    public void printDiagnostics(){

    }

    public void exitOverlay(){
        try {
            this.registryConnection.sendData(new OverlayNodeSendsDeregistration(this.tcpServer.getHostBytes(), this.tcpServer.getPort(), this.id).pack());
        } catch (Exception e){

        }
    }

}