package cs455.overlay.wireformats;

import java.io.IOException;

public class RegistryRequestsTaskInitiate extends Event {

    private int type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    int numDataPackets;

    public RegistryRequestsTaskInitiate(int numDataPackets){
        this.numDataPackets = numDataPackets;
    }

    public RegistryRequestsTaskInitiate(){
        // nothing
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = new byte[5];
        data[0] = (byte)type;
        data[1] = (byte)(numDataPackets >> 24);
        data[2] = (byte)(numDataPackets >> 16);
        data[3] = (byte)(numDataPackets >> 8);
        data[4] = (byte)(numDataPackets);
        return data;
    }

    @Override
    public void craft(byte[] b) {
        type = b[0];
        numDataPackets = (b[1] & 0xFF);
        numDataPackets <<= 8;
        numDataPackets |= (b[2] & 0xFF);
        numDataPackets <<= 8;
        numDataPackets |= (b[3] & 0xFF);
        numDataPackets <<= 8;
        numDataPackets |= (b[4] & 0xFF);
    }

    @Override
    public int getType(){
        return this.type;
    }

    public int getNumDataPackets(){
        return this.numDataPackets;
    }

    public static void main(String[] args) throws Exception {
        RegistryRequestsTaskInitiate RRTI = new RegistryRequestsTaskInitiate(299);
        byte[] data = RRTI.pack();
        RegistryRequestsTaskInitiate RRTI2 = new RegistryRequestsTaskInitiate();
        RRTI2.craft(data);
        System.out.println(RRTI2.getNumDataPackets());
    }

}
