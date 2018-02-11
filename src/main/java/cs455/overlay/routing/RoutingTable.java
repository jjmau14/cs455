package cs455.overlay.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author: Josh Mau | 1/23/2018
 * Class: RoutingTable
 * Maintains a sorted ArrayList of Routes assigned to each node by the registry.
 * The routing table has a max size of 256 (1 byte) as the wireformat RegistrySendsNodeManifest
 * defines routing table size to exactly 1 byte.
 */
public class RoutingTable {

    private ArrayList<Route> routes;

    public RoutingTable(){
        this.routes = new ArrayList<>();

    }

    /**
     * @Method: addRoute
     * Adds a Route object to the routing table ensuring the routing table is ordered
     * by route id (low to high) as assigned by the Registry.
     * */
    public void addRoute(Route r) throws Exception {
        if (this.routes.size() < 256) {
            this.routes.add(r);
            Collections.sort(routes, new Comparator<Route>() {
                @Override
                public int compare(Route o1, Route o2) {
                    if (o1.getGuid() >= o2.getGuid()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        } else {
            throw new Exception("One or more Routing Table is at capacity");
        }
    }

    /**
     * @Getters
     * getTableSize: Returns table size as a byte. Max table size is set to 256.
     * getRoute: Gets Route object from routing table at `index`
     */
    public byte getTableSize(){ return (byte)this.routes.size(); }
    public Route getRoute(int index){ return routes.get(index); }

    /**
     * @Method: toString
     * "Pretty" prints a table containing all ip addresses, ports#, and GUIDs of each
     * route in this instance of the routing table.
     * */
    public String toString() {
        return "";
    }

}
