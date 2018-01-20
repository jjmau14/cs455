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
        this.ip = Arrays.copyOfRange(data, 2, 2+length);
        byte[] portArray = Arrays.copyOfRange(data, 2+length, 6+length);
        int port = 0;
        for (int i = 0 ; i < 4 ; i++) {
            port <<= 8;
            port |= (int) portArray[i] & 0xFF;
        }
        this.port = port;
        System.out.println(this.ipToString() + ":" + port);

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

    public byte[] getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
