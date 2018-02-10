package cs455.overlay.routing;

import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;

public class RegisterItem {

    private byte[] ip;
    private int port;
    private int id;
    private boolean overlayReady = false;
    private boolean taskComplete = false;
    public OverlayNodeReportsTrafficSummary ONRTS;

    public RegisterItem(byte[] ip, int port){
        this.ONRTS = new OverlayNodeReportsTrafficSummary();
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

    public void setReady(){
        this.overlayReady = true;
    }

    public void setComplete(){
        this.taskComplete = true;
    }

    public boolean isComplete(){
        return this.taskComplete;
    }

    public void unsetComplete(){
        this.taskComplete = false;
    }

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