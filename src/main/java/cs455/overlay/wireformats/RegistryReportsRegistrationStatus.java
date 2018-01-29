package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RegistryReportsRegistrationStatus extends Event {

    private byte type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    private int id;
    private byte length;
    private byte[] message;

    public RegistryReportsRegistrationStatus(int id, String message){
        this.id = id;
        this.message = message.getBytes();
        this.length = (byte)message.getBytes().length;
    }

    public RegistryReportsRegistrationStatus(){
        // Nothing
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.write(type);
        dout.writeInt(id);
        dout.write(length);
        dout.write(message, 0, length);

        dout.flush();
        data = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return data;
    }

    @Override
    public void craft(byte[] data) {
        byte[] idArray = Arrays.copyOfRange(data, 1, 1+4);
        id = 0;
        for (int i = 0 ; i < 4 ; i++){
            id <<= 8;
            id |= (int) idArray[i] & 0xFF;
        }
        int length = (data[5] & 0xFF);
        message = Arrays.copyOfRange(data, 6, 6 + length);
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
