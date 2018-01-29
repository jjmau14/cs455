package cs455.overlay.wireformats;

import cs455.overlay.routing.Route;
import cs455.overlay.routing.RoutingTable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class RegistrySendsNodeManifest extends Event{

    byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    RoutingTable routes;
    int[] nodes;

    public RegistrySendsNodeManifest(RoutingTable routes, int[] nodes){
        this.routes = routes;
        this.nodes = nodes;
    }

    public RegistrySendsNodeManifest(){
        // Nothing - for use by craft.
    }

    @Override
    public byte[] pack() {
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
        return data;
    }

    @Override
    public void craft(byte[] data) {

        int index = 1; // ignore type

        byte tableSize = data[index++];
        this.routes = new RoutingTable();
        for (int i = 0 ; i < tableSize ; i++){
            ByteBuffer guid = ByteBuffer.wrap(new byte[]{data[index], data[index+1], data[index+2], data[index+3]});
            index += 4; // for guid
            byte ipSize = data[index++];
            byte[] ip = new byte[ipSize];
            for (int j = 0 ; j < ipSize ; j++){
                ip[j] = data[index++];
            }
            ByteBuffer port = ByteBuffer.wrap(new byte[]{data[index], data[index+1], data[index+2], data[index+3]});
            index += 4; // for port
            try {
                routes.addRoute(new Route(ip, port.getInt(), guid.getInt()));
            } catch (Exception e){

            }

        }

        byte nodeSize = data[index++];
        this.nodes = new int[nodeSize];
        for (int i = 0 ; i < nodeSize ; i++){
            ByteBuffer byteNodes = ByteBuffer.wrap(new byte[]{data[index], data[index+1], data[index+2], data[index+3]});
            index += 4;
            this.nodes[i] = byteNodes.getInt();
        }

    }

    public RoutingTable getRoutes() {
        return routes;
    }

    @Override
    public int getType() {
        return type;
    }

    public int[] getNodes() {
        return nodes;
    }
}
