package cs455.overlay.wireformats;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NodeReportsOverlaySetupStatus extends Event {

    private byte type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    private int statusOrId;
    private String message;

    public NodeReportsOverlaySetupStatus(int statusOrId, String message){
        this.statusOrId = statusOrId;
        this.message = message;
    }

    public NodeReportsOverlaySetupStatus(){
        // Nothing
    }

    @Override
    public byte[] pack() throws IOException {
        byte[] data = new byte[1+4+1+this.message.getBytes().length];
        int index = 0;
        data[index++] = (byte)this.type;
        data[index++] = (byte)(this.statusOrId >> 24);
        data[index++] = (byte)(this.statusOrId >> 16);
        data[index++] = (byte)(this.statusOrId >> 8);
        data[index++] = (byte)(this.statusOrId);
        data[index++] = (byte)this.message.getBytes().length;
        byte[] messageBytes = this.message.getBytes();
        for (int i = 0 ; i < messageBytes.length ; i++){
            data[index++] = messageBytes[i];
        }
        return data;
    }

    @Override
    public void craft(byte[] b) {
        int index = 0;
        this.type = b[index++];
        ByteBuffer status = ByteBuffer.wrap(new byte[]{b[index], b[index+1], b[index+2], b[index+3]});
        index += 4;
        this.statusOrId = status.getInt();
        int length = b[index++];
        String message = new String(Arrays.copyOfRange(b, index, index+length));
        this.message = message;
    }

    @Override
    public int getType(){
        return this.type;
    }

    public String getMessage(){
        return this.message;
    }

    public int getStatusOrId(){
        return this.statusOrId;
    }

    public static void main(String[] args) throws Exception {
        NodeReportsOverlaySetupStatus NROSS = new NodeReportsOverlaySetupStatus(1,"OK");
        byte[] data = NROSS.pack();
        System.out.println(Arrays.toString(data));
        NodeReportsOverlaySetupStatus NROSS2 = new NodeReportsOverlaySetupStatus();
        NROSS2.craft(data);
        System.out.println(NROSS2.getType());
        System.out.println(NROSS2.getStatusOrId());
        System.out.println(NROSS2.getMessage());
        System.out.println(NROSS2.getMessage().length());
    }

}
