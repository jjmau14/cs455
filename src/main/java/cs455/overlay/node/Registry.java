package cs455.overlay.node;

import cs455.overlay.routing.RegisterItem;
import cs455.overlay.routing.Route;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.util.CommandParser;
import cs455.overlay.wireformats.*;

import java.util.Hashtable;

public class Registry extends Node{

    private Hashtable<Integer, RegisterItem> registry;
    private RoutingTable[] manifests;
    private TCPConnectionsCache cache;
    private TCPServerThread tcpServer;
    private int port;
    private Long totalSent = 0l;
    private Long totalReceived = 0l;
    private Integer count = 0;
    private Integer completeCount = 0;
    private Integer totalPacketsSent = 0;
    private Integer totalPacketsReceived = 0;
    private OverlayNodeReportsTrafficSummary overlaySummary;

    public static void main(String[] args) throws Exception {
        if (args.length != 1){
            System.out.println("USAGE: java cs455.overlay.node.Registry [Port Number]");
            System.exit(1);
        }
        Registry registry = new Registry(Integer.parseInt(args[0]));
        registry.init();

    }

    public Registry(int port) throws Exception {
        this.cache = new TCPConnectionsCache();
        this.registry = new Hashtable<>();
        this.port = port;
    }

    public void init() throws Exception {
        new Thread(this.tcpServer = new TCPServerThread(port), "Registry").start();
        new Thread(() -> new CommandParser().registryParser(this), "Command Parser").start();
        new EventFactory(this);
        overlaySummary = new OverlayNodeReportsTrafficSummary();
    }

    public void onEvent(TCPConnection conn, Event e) throws Exception {
        switch (e.getType()){

            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                int id = -1;
                String message = "";
                OverlayNodeSendsRegistration ONSR = (OverlayNodeSendsRegistration)e;

                try {
                    id = register(new RegisterItem(ONSR.getIp(), ONSR.getPort()));
                    message = "Registration request successful. There are currently (" + registry.size() + ") nodes constituting the overlay.";
                    this.cache.addConnection(id, conn);
                } catch (Exception err) {
                    System.out.println(err);
                    message = err.getMessage();
                }

                RegistryReportsRegistrationStatus RRRS = new RegistryReportsRegistrationStatus(id, message);
                try {
                    conn.sendData(RRRS.pack());
                } catch (Exception err){
                    System.out.println(err);
                }
                break;


            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                NodeReportsOverlaySetupStatus NROSS = (NodeReportsOverlaySetupStatus)e;
                if (NROSS.getStatusOrId() != -1) {
                    this.registry.get(NROSS.getStatusOrId()).setReady();
                    //System.out.println("Node id: " + NROSS.getStatusOrId() + ": " + NROSS.getMessage() + ".. Node set to ready state.");
                }
                break;


            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                OverlayNodeReportsTaskFinished ONRTF = (OverlayNodeReportsTaskFinished)e;
                this.registry.get(ONRTF.getGuid()).setComplete();
                //System.out.println("Task complete for node " + ONRTF.getGuid());
                synchronized (this.completeCount) {
                    completeCount += 1;
                    if (completeCount == this.registry.size()) {
                        gatherTaskData();
                    }
                }
                break;


            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                OverlayNodeReportsTrafficSummary ONRTS = (OverlayNodeReportsTrafficSummary)e;
                synchronized (this.overlaySummary) {
                    this.overlaySummary.addSumReceived(ONRTS.getSumReceived());
                    this.overlaySummary.addSumSent(ONRTS.getSumSent());
                    this.overlaySummary.addPacketsReceived(ONRTS.getPacketsReceived());
                    this.overlaySummary.addPacketsSent(ONRTS.getPacketsSent());
                    this.overlaySummary.addPacketsRelayed(ONRTS.getPacketsRelayed());
                }
                //System.out.println("Received info for " + ONRTS.getId());
                this.registry.get(ONRTS.getId()).ONRTS.addPacketsRelayed(ONRTS.getPacketsRelayed());
                this.registry.get(ONRTS.getId()).ONRTS.addPacketsReceived(ONRTS.getPacketsReceived());
                this.registry.get(ONRTS.getId()).ONRTS.addPacketsSent(ONRTS.getPacketsSent());
                this.registry.get(ONRTS.getId()).ONRTS.addSumReceived(ONRTS.getSumReceived());
                this.registry.get(ONRTS.getId()).ONRTS.addSumSent(ONRTS.getSumSent());
                synchronized (this.count) {
                    this.count+=1;
                    if (this.count == this.registry.size()) {
                        if (this.overlaySummary.getPacketsSent() == this.overlaySummary.getPacketsReceived()) {
                            System.out.println("\nTask Summary:\n");
                            synchronized (this.overlaySummary) {
                                System.out.print(String.format("| %-8s ", "Node ID"));
                                System.out.print(String.format("| %-12s ", "Packets Sent"));
                                System.out.print(String.format("| %-16s ", "Packets Received"));
                                System.out.print(String.format("| %-15s ", "Packets Relayed"));
                                System.out.print(String.format("| %-15s ", "Sum Sent"));
                                System.out.println(String.format("| %-15s |", "Sum Received"));
                                System.out.println("====================================================================================================");
                                for (Integer i : this.registry.keySet()) {
                                    System.out.print(String.format("| %-8s |", i));
                                    System.out.print(String.format(" %-12s ", this.registry.get(i).ONRTS.getPacketsSent()));
                                    System.out.print(String.format("| %-16s ", this.registry.get(i).ONRTS.getPacketsReceived()));
                                    System.out.print(String.format("| %-15s ", this.registry.get(i).ONRTS.getPacketsRelayed()));
                                    System.out.print(String.format("| %-15s ", this.registry.get(i).ONRTS.getSumSent()));
                                    System.out.println(String.format("| %-15s |", this.registry.get(i).ONRTS.getSumReceived()));
                                }
                                System.out.println("----------------------------------------------------------------------------------------------------");
                                System.out.print(String.format("| %-8s ", "Sum"));
                                System.out.print(String.format("| %-12s ", this.overlaySummary.getPacketsSent()));
                                System.out.print(String.format("| %-16s ", this.overlaySummary.getPacketsReceived()));
                                System.out.print(String.format("| %-15s ", this.overlaySummary.getPacketsRelayed()));
                                System.out.print(String.format("| %-15s ", this.overlaySummary.getSumReceived()));
                                System.out.println(String.format("| %-15s |", this.overlaySummary.getSumSent()));
                                this.overlaySummary.reset();
                                this.count = 0;
                            }
                        } else {
                            System.out.println(this.overlaySummary.getPacketsReceived() + "/" + this.overlaySummary.getPacketsSent());
                            synchronized (this.overlaySummary){
                                this.overlaySummary.reset();
                            }
                            this.count = 0;
                            System.out.println("Packets did not match. Resending request for summary...");
                            Thread.sleep(2500);
                            gatherTaskData();
                        }
                    }
                }
                break;

            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                OverlayNodeSendsDeregistration ONSD = (OverlayNodeSendsDeregistration)e;
                RegisterItem reg = this.registry.remove(ONSD.getId());
                if (reg != null) {
                    this.cache.getConnectionById(ONSD.getId()).sendData(new RegistryReportsDeregistrationStatus((byte) 1).pack());
                    this.cache.delete(ONSD.getId());
                } else {
                    this.cache.getConnectionById(ONSD.getId()).sendData(new RegistryReportsDeregistrationStatus((byte) 0).pack());
                }
                break;
        }

    }

    /**
     * @method: Synchronized register:
     *  two nodes will not have the same ID if two
     *  nodes request registration at the same time.
     *  Must setID as current size first (since 0 based), then add the item
     *  to the registry so the size will be incremented next time.
     * */
    private synchronized int register(RegisterItem ri) throws Exception {
        if (!registry.containsValue(ri)){
            if (registry.size() < 127) {
                for (int i = 0 ; i < 127 ; i++){
                    if (this.registry.get(i) == null){
                        ri.setId(i);
                        registry.put(i, ri);
                        break;
                    }
                }
            } else {
                throw new Exception("Registry is full.");
            }
        } else {
            throw new Exception("This node is already registered");
        }
        return ri.getId();
    }

    public void generateManifests(int size) throws Exception {
        if (this.registry.size() != 0) {
            this.manifests = new RoutingTable[registry.size()];

            for (int i = 0; i < registry.size(); i++) {

                RoutingTable temp = new RoutingTable();

                for (int j = 0; j < size; j++) {
                    int pow = (int) Math.pow(2, j);
                    int index = pow + i;

                    if (index < registry.size()) {
                        RegisterItem ri = registry.get(index);
                        temp.addRoute(new Route(ri.getIp(), ri.getPort(), ri.getId()));
                    } else {
                        index -= registry.size();
                        RegisterItem ri = registry.get(index);
                        temp.addRoute(new Route(ri.getIp(), ri.getPort(), ri.getId()));
                    }
                }
                manifests[i] = temp;
            }

            this.cache.doForEach((Integer id) -> {
                try {
                    RoutingTable r = this.manifests[id];
                    RegistrySendsNodeManifest RSNM = new RegistrySendsNodeManifest(r, this.getAllNodes());
                    this.cache.getConnectionById(id).sendData(RSNM.pack());
                } catch (Exception e) {
                    ;
                }
                return true;
            });
        } else {
            System.out.println("No nodes have been registered with the system.");
        }
    }

    public void printManifests(){
        if (this.manifests != null) {
            int counter = 0;
            synchronized (System.out) {
                System.out.println();
                for (RoutingTable r : manifests) {
                    System.out.println("Node " + counter++);
                    System.out.println(r.toString());
                }
            }
        } else {
            System.out.println("No overlay has been configured. Use command \"setup-overlay [manifest size]\" to configure an overlay.");
        }
    }

    public void listMessagingNodes(){
        System.out.println();
        System.out.print(String.format("| %-17s ", "Host"));
        System.out.print(String.format("| %-7s ", "Port"));
        System.out.println(String.format("| %-7s |", "Node ID"));
        System.out.println("=========================================");
        for (Integer i : this.registry.keySet()) {
            if (this.registry.get(i) != null) {
                System.out.print(String.format("| %-17s ", registry.get(i).ipToString()));
                System.out.print(String.format("| %-7s ", registry.get(i).getPort()));
                System.out.println(String.format("| %-7s |", registry.get(i).getId()));
            }
        }
        System.out.println("-----------------------------------------");
        System.out.println("There " + (this.getSize() == 1 ? "is 1 node registered with the registry." :
                "are " + this.getSize() + " nodes registered with the registry."));
        System.out.println();
    }

    public Hashtable<Integer, RegisterItem> getRegistry() { return registry; }
    public int getSize() {
        return registry.size();
    }
    public int[] getAllNodes() {
        int[] nodes = new int[this.getSize()];
        int counter = 0;
        for (Integer key : this.registry.keySet()){
            nodes[counter++] = key;
        }
        return nodes;
    }

    public void initDataStream(int numDataPackets){
        for (Integer i : this.registry.keySet()){
            this.registry.get(i).ONRTS.reset();
        }
        synchronized (this.completeCount) {
            this.completeCount = 0;
        }
        this.cache.doForAll((TCPConnection conn) -> {
            try {
                conn.sendData(new RegistryRequestsTaskInitiate(numDataPackets).pack());
            } catch (Exception e){
                ;
            }
            return true;
        });
    }

    private void gatherTaskData(){
        this.cache.doForAll((TCPConnection conn) -> {
            try {
                conn.sendData(new RegistryRequestsTrafficSummary().pack());
            } catch (Exception e){
                System.out.println("[" + Thread.currentThread().getName() + "] Error requesting task summary: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        });
    }
}
