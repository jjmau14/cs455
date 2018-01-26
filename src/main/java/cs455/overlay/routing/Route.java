package cs455.overlay.routing;

import java.nio.ByteBuffer;

/**
 * @author: Josh Mau | 1/23/2018
 * Class: Route
 * Maintains instance data containing IP address, port#, and a GUID (Global Unique Identifier)
 * assigned to an ip address or node by the Registry.
 * */
public class Route {

    private byte[] ip;
    private int port;
    private int guid;

    public Route(byte[] ip, int port, int guid){
        this.ip = ip;
        this.port = port;
        this.guid = guid;
    }

    /**
     * @Getters
     * getIP: Returns ip address as a byte array of size 4.
     * getGuid: Returns global unique ID assigned by the Registry of that particular node.
     * getGuidBytes: Returns the byte array version of the guid.
     * getPort: Returns the port of a node.
     * */
    public byte[] getIp() {
        return ip;
    }
    public int getGuid() {
        return guid;
    }
    public byte[] getGuidBytes() {
        byte guidByte[] = new byte[4];
        ByteBuffer buf = ByteBuffer.wrap(guidByte);
        buf.putInt(this.guid);
        return guidByte;
    }
    public byte[] getPortBytes() {
        byte portByte[] = new byte[4];
        ByteBuffer buf = ByteBuffer.wrap(portByte);
        buf.putInt(this.port);
        return portByte;
    }
    public int getPort() {
        return port;
    }

    /**
     * @Method: ipToString
     * Returns "pretty" version of the ip address (ie. 192.168.40.23).
     * Mostly used for printing routing tables.
     * */
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
}
