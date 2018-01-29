package cs455.overlay.wireformats;

import java.io.IOException;

public class OverlayNodeReportsTrafficSummary extends Event {

    @Override
    public byte[] pack() throws IOException {
        return new byte[0];
    }

    @Override
    public void craft(byte[] b) {

    }

    @Override
    public int getType(){
        return 0;
    }

}
