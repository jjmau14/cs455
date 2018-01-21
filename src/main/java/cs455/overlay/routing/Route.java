package cs455.overlay.routing;

public class Route {

    private byte[] ip;
    private int port;
    private int guid;

    public Route(byte[] ip, int port, int guid){
        this.ip = ip;
        this.port = port;
        this.guid = guid;
    }

    public byte[] getIp() {
        return ip;
    }

    public int getGuid() {
        return guid;
    }

    public int getPort() {
        return port;
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
}
