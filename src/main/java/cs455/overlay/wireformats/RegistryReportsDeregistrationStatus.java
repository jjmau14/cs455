package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryReportsDeregistrationStatus extends Event {

    byte type = Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    byte successStatus;

    public RegistryReportsDeregistrationStatus(byte successStatus){
        this.successStatus = successStatus;
    }

    public RegistryReportsDeregistrationStatus(){
        // Nothing.
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = new byte[2];
        data[0] = type;
        data[1] = successStatus;
        return data;
    }

    @Override
    public void craft(byte[] data) {
        this.type = data[0];
        this.successStatus = data[1];
    }

    @Override
    public int getType(){
        return this.type;
    }
    public byte getStatus(){ return this.successStatus; }
}
