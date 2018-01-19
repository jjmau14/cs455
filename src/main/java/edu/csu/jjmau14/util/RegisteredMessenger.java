package edu.csu.jjmau14.util;

public class RegisteredMessenger {

    private String ipString;
    private byte[] ipBytes;
    private int port;
    private int id;

    public RegisteredMessenger(byte[] ipBytes, int port){
        this.ipBytes = ipBytes;
        this.port = port;
        this.ipString = "";
        for (int i = 0 ; i < ipBytes.length - 1; i++){
            this.ipString += (ipBytes[i] & 0xFF) + ".";
        }
        this.ipString += (ipBytes[ipBytes.length-1] & 0xFF) + ":" + port;
    }

    public int getPort() {
        return port;
    }

    public byte[] getIpBytes(){
        return this.ipBytes;
    }

    public byte getIpByteByIndex(int i){
        return this.ipBytes[i];
    }

    @Override
    public boolean equals(Object o){
        RegisteredMessenger other = (RegisteredMessenger) o;
        for (int i = 0 ; i < other.getIpBytes().length ; i++){
            if (this.getIpByteByIndex(i) != other.getIpByteByIndex(i)){
                return false;
            }
        }
        if (this.getPort() != other.getPort()){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ipString;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public String getIpString(){
        return this.ipString.substring(0, this.ipString.indexOf(":"));
    }
}
