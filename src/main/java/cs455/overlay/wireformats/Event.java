package cs455.overlay.wireformats;

import java.io.IOException;

public abstract class Event {

    private int type;

    public abstract byte[] pack() throws IOException;
    public abstract void craft(byte[] b);
    public abstract int getType();
}
