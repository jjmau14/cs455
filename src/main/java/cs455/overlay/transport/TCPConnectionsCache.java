package cs455.overlay.transport;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author: Josh Mau | 1/26/2018
 * Class: TCPConnectionCache: Maintains hash map of integer node IDs to open socket connections.
 * */
public class TCPConnectionsCache {

    HashMap<Integer, TCPConnection> connections;

    public TCPConnectionsCache(){
        this.connections = new HashMap<>();
    }

    public void addConnection(int id, TCPConnection connection){
        if (!this.connections.containsKey(id)) {
            //System.out.println("Connection saved from " + connection.getSocket().getLocalPort() + " to " + connection.getSocket().getPort());
            this.connections.put(id, connection);
        }
    }

    public TCPConnection getConnectionById(int id){
        return this.connections.get(id);
    }

    public void doForAll(Predicate<TCPConnection> func){
        for (Map.Entry<Integer, TCPConnection> entry : connections.entrySet()){
            func.test(connections.get(entry.getKey()));
        }
    }

    public void doForEach(Predicate<Integer> func){
        for (Map.Entry<Integer, TCPConnection> entry : connections.entrySet()){
            func.test(entry.getKey());
        }
    }

    public TCPConnection getNearestId(int id){
        int currentMin = -1;
        int currentMax = -1;
        for (Map.Entry<Integer, TCPConnection> entry : connections.entrySet()){
            int val = entry.getKey().intValue();
            if (val == id){
                return this.connections.get(val);
            }
            if (val < id){
                currentMin = val;
            }
            if (val > currentMax) {
                currentMax = val;
            }
        }
        if (currentMin == -1)
            return this.connections.get(currentMax);
        return this.connections.get(currentMin);
    }

    public void delete(int id){
        this.connections.remove(id);
    }

}
