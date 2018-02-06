package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummary extends Event {

    private byte type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    private int id;
    private int packetsSent;
    private int packetsRelayed;
    private long sumSent;
    private int packetsReceived;
    private long sumReceived;

    public OverlayNodeReportsTrafficSummary(int id, int packetsSent, int packetsRelayed,
                                            long sumSent, int packetsReceived, long sumReceived) {
        this.id = id;
        this.packetsSent = packetsSent;
        this.packetsRelayed = packetsRelayed;
        this.sumSent = sumSent;
        this.packetsReceived = packetsReceived;
        this.sumReceived = sumReceived;
    }

    public OverlayNodeReportsTrafficSummary(){
        // Nothing
    }

    @Override
    public byte[] pack() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.writeByte(this.type);
        dout.writeInt(this.id);
        dout.writeInt(this.packetsSent);
        dout.writeInt(this.packetsRelayed);
        dout.writeLong(this.sumSent);
        dout.writeInt(this.packetsReceived);
        dout.writeLong(sumReceived);
        dout.flush();

        byte[] data = bout.toByteArray();
        bout.close();
        dout.close();

        return data;

    }

    @Override
    public void craft(byte[] data) {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

        try {
            this.type = din.readByte();
            this.id = din.readInt();
            this.packetsSent = din.readInt();
            this.packetsRelayed = din.readInt();
            this.sumSent = din.readLong();
            this.packetsReceived = din.readInt();
            this.sumReceived = din.readLong();

            bin.close();
            din.close();
        } catch (Exception e){

        }
    }

    @Override
    public int getType(){
        return this.type;
    }

}
