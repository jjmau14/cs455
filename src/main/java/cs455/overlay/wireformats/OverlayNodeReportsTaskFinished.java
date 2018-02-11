package cs455.overlay.wireformats;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.InetAddress;

public class OverlayNodeReportsTaskFinished extends Event {

    private int type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    private byte[] ip;
    private int port;
    private int guid;

    public OverlayNodeReportsTaskFinished(byte[] ip, int port, int guid){
        this.ip = ip;
        this.port = port;
        this.guid = guid;
    }

    public OverlayNodeReportsTaskFinished(){
        // Nothing
    }

    @Override
    public byte[] pack() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.writeByte(this.type);
        dout.writeByte(this.ip.length);
        for (int i = 0 ; i < this.ip.length ; i++){
            dout.writeByte(this.ip[i]);
        }
        dout.writeInt(this.port);
        dout.writeInt(this.guid);
        dout.flush();

        return bout.toByteArray();
    }

    @Override
    public void craft(byte[] data) {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

        try {
            this.type = din.readByte();
            int length = din.readByte();
            this.ip = new byte[length];
            din.readFully(this.ip);
            this.port = din.readInt();
            this.guid = din.readInt();
        } catch (Exception e){

        }
    }

    @Override
    public int getType(){
        return this.type;
    }
    public String ipToString(){
        String ipString = "";
        for (int i = 0 ; i < this.ip.length; i++){
            if (i < this.ip.length -1)
                ipString += (this.ip[i] & 0xFF) + ".";
            else
                ipString += (this.ip[i] & 0xFF);
        }
        return ipString;
    }
    public int getPort(){ return this.port; }
    public int getGuid(){ return this.guid; }

    public static void main(String[] args) throws Exception {
        OverlayNodeReportsTaskFinished ONRTF = new OverlayNodeReportsTaskFinished(InetAddress.getLocalHost().getAddress(), 1, 2);
        System.out.println(ONRTF.ipToString());
        OverlayNodeReportsTaskFinished ONRTF2 = new OverlayNodeReportsTaskFinished();
        ONRTF2.craft(ONRTF.pack());
        System.out.println(ONRTF2.ipToString());
        System.out.println(ONRTF2.getGuid());
        System.out.println(ONRTF2.getPort());
        assert(ONRTF2.getType() == Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED);
    }

}
