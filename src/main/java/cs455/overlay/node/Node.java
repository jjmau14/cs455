package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;

import java.net.Socket;

public abstract class Node {

    public abstract void onEvent(TCPConnection conn, Event e);
}
