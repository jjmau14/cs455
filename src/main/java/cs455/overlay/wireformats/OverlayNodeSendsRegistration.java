package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeSendsRegistration implements Protocol {

    private byte type = OVERLAY_NODE_SENDS_REGISTRATION;
    private byte length;
    private byte[] ip;
    int port;

    public OverlayNodeSendsRegistration(byte[] ip, int port){
        this.length = (byte)ip.length;
        this.ip = ip;
        this.port = port;
    }

    public OverlayNodeSendsRegistration(){
        // Empty object to cast
    }

    public byte[] pack() throws IOException{
        byte[] data = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.write(type);
        dout.write(length);
        dout.write(ip);
        dout.writeInt(port);

        dout.flush();
        data = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return data;
    }

    public void craft(byte[] data) throws IOException {
        byte length = data[1];
        String ip = "";
        for (int i = 0 ; i < length; i++){
            if (i < length -1)
                ip += (data[i+2] & 0xFF) + ".";
            else
                ip += (data[i+2] & 0xFF);
        }
        System.out.println(ip);

    }

}
