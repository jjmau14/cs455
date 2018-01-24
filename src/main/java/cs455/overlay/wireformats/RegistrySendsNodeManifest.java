package cs455.overlay.wireformats;

import cs455.overlay.routing.Route;
import cs455.overlay.routing.RoutingTable;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class RegistrySendsNodeManifest {

    byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    RoutingTable routes;
    int[] nodes;

    public RegistrySendsNodeManifest(RoutingTable routes, int[] nodes){
        this.routes = routes;
        this.nodes = nodes;
    }

    public byte[] pack() throws Exception {
        byte tableSize = routes.getTableSize();
        byte ipSize = (byte) routes.getRoute(0).getIp().length;
        byte[] data = new byte[1+1+(tableSize * (9+ipSize)) + 1 + (4*nodes.length)];
        int index = 0;
        try {
            data[index++] = this.type;
            data[index++] = tableSize;

            for (int i = 0 ; i < routes.getTableSize() ; i++){
                Route r = routes.getRoute(i);
                byte[] guidBytes = r.getGuidBytes();
                data[index++] = guidBytes[0];
                data[index++] = guidBytes[1];
                data[index++] = guidBytes[2];
                data[index++] = guidBytes[3];
                data[index++] = (byte)r.getIp().length;
                byte[] ipBytes = r.getIp();
                for (int j = 0 ; j < (byte) r.getIp().length ; j++){
                    data[index++] = ipBytes[j];
                }
                byte[] portBytes = r.getPortBytes();
                data[index++] = portBytes[0];
                data[index++] = portBytes[1];
                data[index++] = portBytes[2];
                data[index++] = portBytes[3];
            }
            data[index++] = (byte)nodes.length;
            for (int i = 0 ; i < (byte)nodes.length ; i++){
                data[index++] = (byte)(nodes[i] >> 24);
                data[index++] = (byte)(nodes[i] >> 16);
                data[index++] = (byte)(nodes[i] >> 8);
                data[index++] = (byte)(nodes[i]);
            }

        } catch (IndexOutOfBoundsException ioobe){
            throw new IndexOutOfBoundsException("Routing table may be empty: " + ioobe.getMessage());
        }
        System.out.println(Arrays.toString(data));
        System.out.println("SIZE SENT: " + data.length + ": " + (index-1));
        return data;
    }

    public void craft(byte[] data){
        int index = 1; // ignore type

        byte tableSize = data[index++];
        ArrayList<Route> routes = new ArrayList<>();
        for (int i = 0 ; i < tableSize ; i++){
            int guid = 0;
            guid |= data[index++]; // 0
            guid <<= 8;
            guid |= data[index++]; // 1
            guid <<= 8;
            guid |= data[index++]; // 2
            guid <<= 8;
            guid |= data[index++]; // 3
            byte ipSize = data[index++];
            byte[] ip = new byte[ipSize];
            for (int j = 0 ; j < ipSize ; j++){
                ip[j] = data[index++];
            }
            int port = 0;
            port |= data[index++]; // 0
            port <<= 8;
            port |= data[index++]; // 1
            port <<= 8;
            port |= data[index++]; // 2
            port <<= 8;
            port |= data[index++]; // 3
            routes.add(new Route(ip, port, guid));

        }

        byte nodesSzie = data[index++];
        int[] nodes = new int[nodesSzie];
        for (int i = 0 ; i < nodesSzie ; i++){
            int node = data[index++]; // 0
            node <<= 8;
            node |= data[index++]; // 1
            node <<= 8;
            node |= data[index++]; // 2
            node <<= 8;
            node |= data[index++]; // 3
            nodes[i] = (node & 0xFF);
        }
        System.out.println("SIZE:" + (index -1));
        System.out.println(routes.get(0).ipToString());
        System.out.println(routes.get(0).getGuid());
        System.out.println(routes.get(0).getPort());

        System.out.println(Arrays.toString(nodes));

    }

}
