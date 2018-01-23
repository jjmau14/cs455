package cs455.overlay.routing;

import dnl.utils.text.table.TextTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoutingTable {

    private ArrayList<Route> routes;

    public RoutingTable(){
        this.routes = new ArrayList<>();
    }

    public void addRoute(Route r){
        this.routes.add(r);
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                if (o1.getGuid() >= o2.getGuid()){
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    public byte tableSize(){
        return (byte)this.routes.size();
    }

    public String toString(){
        String[][] data = new String[routes.size()][3];
        for (int i = 0 ; i < routes.size() ; i++){
            data[i][0] = Integer.toString(routes.get(i).getGuid());
            data[i][1] = routes.get(i).ipToString();
            data[i][2] = Integer.toString(routes.get(i).getPort());
        }
        TextTable tt = new TextTable(new String[] {"ID", "IP", "Port"}, data);
        tt.printTable();
        return "";
    }

}
