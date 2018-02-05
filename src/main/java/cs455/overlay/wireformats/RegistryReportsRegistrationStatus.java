package cs455.overlay.wireformats;

import java.io.*;
import java.util.Arrays;

public class RegistryReportsRegistrationStatus extends Event {

    private byte type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    private int id;
    private String message;

    public RegistryReportsRegistrationStatus(int id, String message){
        this.id = id;
        this.message = message;
    }

    public RegistryReportsRegistrationStatus(){
        // Nothing
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.write(type);
        dout.writeInt(id);
        dout.write(this.message.getBytes().length);
        dout.write(message.getBytes(), 0, this.message.getBytes().length);

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
            this.id = din.readInt();
            int length = din.readByte();
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
    public int getId() {
        return id;
    }
    public String getMessage() {
        return new String(message);
    }
}
