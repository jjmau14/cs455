package cs455.overlay.wireformats;

import java.io.*;
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
        byte[] data = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.writeByte(this.type);
        dout.writeInt(this.destinationId);
        dout.writeInt(this.sourceId);
        dout.writeInt(this.payload);
        dout.writeInt(this.hopCount);
        for (int i = 0 ; i < this.trace.length ; i++){
            dout.writeInt(this.trace[i]);
        }
        dout.flush();
        data = bout.toByteArray();

        bout.close();
        dout.close();
        return data;
    }

    @Override
    public void craft(byte[] b) {
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(b);
            DataInputStream din = new DataInputStream(new BufferedInputStream(bin));
            this.type = din.readByte();
            this.destinationId = din.readInt();
            this.sourceId = din.readInt();
            this.payload = din.readInt();
            this.hopCount = din.readInt();
            this.trace = new int[hopCount];
            for (int i = 0 ; i < this.trace.length ; i++){
                this.trace[i] = din.readInt();
            }
            bin.close();
            din.close();
        } catch (Exception e){
            ;
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

    public static void main(String[] args) throws Exception {
        OverlayNodeSendsData ONSD = new OverlayNodeSendsData(1,2,3, new int[]{});
        ONSD.addTrace(1);
        System.out.println(Arrays.toString(ONSD.getTrace()));
        OverlayNodeSendsData ONSD2 = new OverlayNodeSendsData();
        ONSD2.craft(ONSD.pack());
        ONSD2.addTrace(2);
        ONSD2.addTrace(2);
        ONSD2.addTrace(2);
        System.out.println(Arrays.toString(ONSD2.getTrace()));
        System.out.println(ONSD2.getType());
    }
}
