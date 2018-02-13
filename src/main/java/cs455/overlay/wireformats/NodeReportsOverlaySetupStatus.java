package cs455.overlay.wireformats;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.util.Arrays;

public class NodeReportsOverlaySetupStatus extends Event {

    private byte type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    private int statusOrId;
    private String message;

    public NodeReportsOverlaySetupStatus(int statusOrId, String message){
        this.statusOrId = statusOrId;
        this.message = message;
    }

    public NodeReportsOverlaySetupStatus(){
        // Nothing
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.writeByte(this.type);
        dout.writeInt(this.statusOrId);
        dout.writeByte(this.message.getBytes().length);
        dout.write(this.message.getBytes(), 0, this.message.getBytes().length);
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
            this.statusOrId = din.readInt();
            byte length = din.readByte();
            byte[] messageBytes = new byte[length];
            din.readFully(messageBytes);
            this.message = new String(messageBytes);

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
    public String getMessage(){ return this.message; }
    public int getStatusOrId(){
        return this.statusOrId;
    }

}
