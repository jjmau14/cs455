package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class RegistryRequestsTaskInitiate extends Event {

    private int type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    int numDataPackets;

    public RegistryRequestsTaskInitiate(int numDataPackets){
        this.numDataPackets = numDataPackets;
    }

    public RegistryRequestsTaskInitiate(){
        // nothing
    }

    @Override
    public byte[] pack() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.writeByte(this.type);
        dout.writeInt(numDataPackets);
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
            this.numDataPackets = din.readInt();

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

    public int getNumDataPackets(){
        return this.numDataPackets;
    }

}
