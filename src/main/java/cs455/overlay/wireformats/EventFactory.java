package cs455.overlay.wireformats;

import cs455.overlay.node.Node;
import cs455.overlay.transport.TCPConnection;

import java.net.Socket;
import java.util.Arrays;

public class EventFactory {

    private static EventFactory eventFactory;
    private static Node node;

    public EventFactory(Node node){
        this.node = node;
        this.eventFactory = this;
    }

    public void run(TCPConnection conn, byte[] b){
        try {
            node.onEvent(conn, getType(b));
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Event getType(byte[] b) throws Exception {
        switch(b[0]){
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                OverlayNodeSendsRegistration ONSR = new OverlayNodeSendsRegistration();
                ONSR.craft(b);
                return ONSR;
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                RegistryReportsRegistrationStatus RRRS = new RegistryReportsRegistrationStatus();
                RRRS.craft(b);
                return RRRS;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                OverlayNodeSendsDeregistration ONSD = new OverlayNodeSendsDeregistration();
                ONSD.craft(b);
                return ONSD;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                RegistryReportsDeregistrationStatus RRDS = new RegistryReportsDeregistrationStatus();
                RRDS.craft(b);
                return RRDS;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                RegistrySendsNodeManifest RSNM = new RegistrySendsNodeManifest();
                RSNM.craft(b);
                return RSNM;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                NodeReportsOverlaySetupStatus NROSS = new NodeReportsOverlaySetupStatus();
                NROSS.craft(b);
                return NROSS;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                RegistryRequestsTaskInitiate RRTI = new RegistryRequestsTaskInitiate();
                RRTI.craft(b);
                return RRTI;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                OverlayNodeSendsData ONSData = new OverlayNodeSendsData();
                ONSData.craft(b);
                return ONSData;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                OverlayNodeReportsTaskFinished ONRTF = new OverlayNodeReportsTaskFinished();
                ONRTF.craft(b);
                return ONRTF;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                RegistryRequestsTrafficSummary RRTS = new RegistryRequestsTrafficSummary();
                RRTS.craft(b);
                return RRTS;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                OverlayNodeReportsTrafficSummary ONTS = new OverlayNodeReportsTrafficSummary();
                ONTS.craft(b);
                return ONTS;
            default:
                throw new Exception("Unrecognized wireformat.");
        }
    }

    public static EventFactory getInstance() {
        return eventFactory;
    }

}
