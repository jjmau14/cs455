package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RegistryReportsRegistrationStatus {

    private byte type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    private int id;
    private byte length;
    private byte[] message;

    public RegistryReportsRegistrationStatus(int id, String message){
        this.id = id;
        this.message = message.getBytes();
        this.length = (byte)message.getBytes().length;
    }

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

    public void craft(byte[] data) throws IOException {
        byte[] idArray = Arrays.copyOfRange(data, 1, 1+4);
        id = 0;
        for (int i = 0 ; i < 4 ; i++){
            id <<= 8;
            id |= (int) idArray[i] & 0xFF;
        }
    }

}
