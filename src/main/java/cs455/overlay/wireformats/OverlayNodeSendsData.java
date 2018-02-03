package cs455.overlay.wireformats;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

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

    public OverlayNodeSendsData(){
        // nothing
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = new byte[1+4+4+4+4+(4*trace.length)];
        int index = 0;
        data[index++] = this.type;
        data[index++] = (byte)(this.destinationId >> 24);
        data[index++] = (byte)(this.destinationId >> 16);
        data[index++] = (byte)(this.destinationId >> 8);
        data[index++] = (byte)(this.destinationId);
        data[index++] = (byte)(this.sourceId >> 24);
        data[index++] = (byte)(this.sourceId >> 16);
        data[index++] = (byte)(this.sourceId >> 8);
        data[index++] = (byte)(this.sourceId);
        data[index++] = (byte)(this.payload >> 24);
        data[index++] = (byte)(this.payload >> 16);
        data[index++] = (byte)(this.payload >> 8);
        data[index++] = (byte)(this.payload);
        data[index++] = (byte)(this.hopCount >> 24);
        data[index++] = (byte)(this.hopCount >> 16);
        data[index++] = (byte)(this.hopCount >> 8);
        data[index++] = (byte)(this.hopCount);

        for (int i = 0 ; i < this.trace.length ; i++){
            data[index++] = (byte)(this.trace[i] >> 24);
            data[index++] = (byte)(this.trace[i] >> 16);
            data[index++] = (byte)(this.trace[i] >> 8);
            data[index++] = (byte)(this.trace[i]);
        }
        return data;
    }

    @Override
    public void craft(byte[] b) {
        int index = 0;
        this.type = b[index++];

        this.destinationId = b[index++];
        this.destinationId <<= 8;
        this.destinationId |= b[index++];
        this.destinationId <<= 8;
        this.destinationId |= b[index++];
        this.destinationId <<= 8;
        this.destinationId |= b[index++];

        this.sourceId = b[index++];
        this.sourceId <<= 8;
        this.sourceId |= b[index++];
        this.sourceId <<= 8;
        this.sourceId |= b[index++];
        this.sourceId <<= 8;
        this.sourceId |= b[index++];

        this.payload = (b[index++] & 0xFF);
        this.payload <<= 8;
        this.payload |= (b[index++] & 0xFF);
        this.payload <<= 8;
        this.payload |= (b[index++] & 0xFF);
        this.payload <<= 8;
        this.payload |= (b[index++] & 0xFF);

        this.hopCount = b[index++];
        this.hopCount <<= 8;
        this.hopCount |= b[index++];
        this.hopCount <<= 8;
        this.hopCount |= b[index++];
        this.hopCount <<= 8;
        this.hopCount |= b[index++];

        this.trace = new int[hopCount];
        for (int i = 0 ; i < hopCount ; i++){
            int temp = b[index++];
            temp <<= 8;
            temp |= b[index++];
            temp <<= 8;
            temp |= b[index++];
            temp <<= 8;
            temp |= b[index++];
            this.trace[i] = temp;
        }
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

    public void addTrace(int id){
        this.hopCount += 1;
        int[] newTrace = new int[this.trace.length+1];
        for (int i = 0 ; i < this.trace.length ; i++){
            newTrace[i] = this.trace[i];
        }
        newTrace[newTrace.length-1] = id;
        this.trace = newTrace;
    }
}
