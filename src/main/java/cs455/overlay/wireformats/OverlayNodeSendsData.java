package cs455.overlay.wireformats;

import java.io.IOException;

public class OverlayNodeSendsData extends Event {

    private byte type = Protocol.OVERLAY_NODE_SENDS_DATA;
    private int destinationId;
    private int sourceId;
    private int payload;
    private int hopCount;
    private int[] trace;

    public OverlayNodeSendsData(int destinationId, int sourceId, int payload, int[] trace){
        this.destinationId = destinationId;
        this.sourceId = sourceId;
        this.payload = payload;
        this.hopCount = 0;
        this.trace = trace;
    }

    @Override
    public byte[] pack() throws IOException {
        return new byte[0];
    }

    @Override
    public void craft(byte[] b) {

    }

    @Override
    public int getType(){
        return this.type;
    }

    public int getDestinationId() { return this.destinationId; }
    public int getHopCount() { return hopCount; }
    public int getPayload() { return payload; }
    public int getSourceId() { return sourceId; }
    public int[] getTrace() { return trace; }
}
