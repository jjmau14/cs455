package cs455.overlay.node;

import cs455.overlay.wireformats.Event;

public abstract class Node {

    public abstract void onEvent(Event e);

}
