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
    private Long sumSent;
    private int packetsReceived;
    private Long sumReceived;

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
        this.packetsSent = 0;
        this.packetsRelayed = 0;
        this.packetsReceived = 0;
        this.sumSent = 0l;
        this.sumReceived = 0l;
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
    public int getId(){ return this.id; }
    public int getPacketsReceived() {
        return packetsReceived;
    }
    public int getPacketsRelayed() {
        return packetsRelayed;
    }
    public int getPacketsSent() {
        return packetsSent;
    }
    public Long getSumReceived() {
        return sumReceived;
    }
    public Long getSumSent() {
        return sumSent;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addPacketsReceived(int packetsReceived) {
        this.packetsReceived += packetsReceived;
    }

    public void addPacketsRelayed(int packetsRelayed) {
        this.packetsRelayed += packetsRelayed;
    }

    public void addPacketsSent(int packetsSent) {
        this.packetsSent += packetsSent;
    }

    public void addSumReceived(long sumReceived) {
        this.sumReceived += sumReceived;
    }

    public void addSumSent(long sumSent) {
        this.sumSent += sumSent;
    }

    public void reset(){
        this.sumReceived = 0l;
        this.sumSent = 0l;
        this.packetsRelayed = 0;
        this.packetsReceived = 0;
        this.packetsSent = 0;
    }
}
