package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class OverlayNodeSendsRegistration extends Event{

    private byte type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
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
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        dout.writeByte(type);
        dout.writeByte(length);
        dout.write(ip);
        dout.writeInt(port);

        dout.flush();
        data = bout.toByteArray();

        bout.close();
        dout.close();
        return data;
    }

    public void craft(byte[] data) {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

        try {
            this.type = din.readByte();
            int length = din.readByte();
            this.ip = new byte[length];
            din.readFully(this.ip);
            this.port = din.readInt();

        } catch (Exception e){

        }

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
    public int getType(){
        return this.type;
    }
}
