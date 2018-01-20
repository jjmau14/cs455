package cs455.overlay.routing;

public class RegisterItem {

    private byte[] ip;
    private int port;

    public RegisterItem(byte[] ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public byte[] getIp() {
        return ip;
    }

    @Override
    public boolean equals(Object o){
        RegisterItem other = (RegisterItem) o;
        for (int i = 0 ; i < this.ip.length ; i++){
            if (this.ip[i] != other.ip[i])
                return false;
        }
        return true;
    }
}