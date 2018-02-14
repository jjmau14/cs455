package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryRequestsTrafficSummary extends Event {

    private byte type = Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;

    @Override
    public byte[] pack() throws IOException {
        return new byte[] { this.type };
    }

    @Override
    public void craft(byte[] data) {
        this.type = data[0];
    }

    @Override
    public int getType(){
        return this.type;
    }

}
