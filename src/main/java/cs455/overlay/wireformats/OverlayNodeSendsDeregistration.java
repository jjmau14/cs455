package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration extends Event {

    /**
     * byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
     * byte: length of following "IP address" field
     * byte[^^]: IP address; from InetAddress.getAddress()
     * int: Port number
     * int: assigned Node ID
     * */
    private byte type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    private byte[] ip;
    private int port;
    private int id;

    public OverlayNodeSendsDeregistration(byte[] ip, int port, int id){
        this.ip = ip;
        this.port = port;
        this.id = id;
    }

    public OverlayNodeSendsDeregistration(){
        // Nothing.
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.writeByte(this.type);
        dout.writeByte(this.ip.length);
        dout.write(this.ip, 0, this.ip.length);
        dout.writeInt(this.port);
        dout.writeInt(id);
        dout.flush();

        data = bout.toByteArray();
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
            int length = din.readByte();
            this.ip = new byte[length];
            din.readFully(this.ip);
            this.port = din.readInt();
            this.id = din.readInt();

        } catch (Exception e){

        }
    }

    @Override
    public int getType(){
        return this.type;
    }
    public int getId(){ return this.id; }
}
