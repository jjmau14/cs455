package cs455.overlay.routing;

public class RegisterItem {

    private byte[] ip;
    private int port;
    private int id;

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

    public int getId(){ return id; }

    public void setId(int id){ this.id = id; }

    @Override
    public boolean equals(Object o){
        RegisterItem other = (RegisterItem) o;
        for (int i = 0 ; i < this.ip.length ; i++){
            if (this.ip[i] != other.ip[i])
                return false;
        }
        if (this.port != other.port)
            return false;
        return true;
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