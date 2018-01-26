package cs455.overlay.transport;

import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPConnection.TCPSender;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author: Josh Mau | 1/26/2018
 * Class: TCPConnectionCache: Maintains hash map of integer node IDs to open socket connections.
 * */
public class TCPConnectionsCache {

    HashMap<Integer, Socket> connections;

    public TCPConnectionsCache(){
        this.connections = new HashMap<>();
    }

    public void addConnection(int id, Socket socket){
        if (!this.connections.containsKey(id))
            this.connections.put(id, socket);
    }

    public Socket getConnectionById(int id){
        return this.connections.get(id);
    }

    public void doForAll(Predicate<Integer> func){
        for (Map.Entry<Integer, Socket> entry : connections.entrySet()){
            func.test(entry.getKey());
        }
    }

}
