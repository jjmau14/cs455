package cs455.overlay.wireformats;

import cs455.overlay.routing.Route;
import cs455.overlay.routing.RoutingTable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;

public class RegistrySendsNodeManifest extends Event{

    byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    RoutingTable routes;
    int[] nodes;

    public RegistrySendsNodeManifest(RoutingTable routes, int[] nodes){
        this.routes = routes;
        this.nodes = nodes;
    }

    public RegistrySendsNodeManifest(){
        this.routes = new RoutingTable();
    }

    @Override
    public byte[] pack() {
        byte[] data = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(bout));

        try {
            dout.writeByte(this.type);
            dout.writeByte(this.routes.getTableSize());

            for (int i = 0 ; i < routes.getTableSize() ; i++){
                Route r = routes.getRoute(i);

                dout.writeInt(r.getGuid());
                dout.writeByte(r.getIp().length);
                for (int j = 0 ; j < r.getIp().length ; j++)
                    dout.writeByte(r.getIp()[j]);
                dout.writeInt(r.getPort());
            }

            dout.writeByte(nodes.length);
            for (int i = 0 ; i < (byte)nodes.length ; i++){
                dout.writeInt(nodes[i]);
            }

            dout.flush();
            data = bout.toByteArray();
            bout.close();
            dout.close();

        } catch (IndexOutOfBoundsException ioobe){
            throw new IndexOutOfBoundsException("Routing table may be empty: " + ioobe.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return data;
    }

    @Override
    public void craft(byte[] data) {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

        try {
            this.type = din.readByte();
            int tableSize = din.readByte();

            for (int i = 0 ; i < tableSize ; i++){
                int guid = din.readInt();
                int length = din.readByte();
                byte[] ip = new byte[length];
                din.readFully(ip, 0, length);
                int port = din.readInt();
                this.routes.addRoute(new Route(ip, port, guid));
            }

            int length = din.readByte();
            this.nodes = new int[length];
            for (int i = 0 ; i < length ; i++){
                this.nodes[i] = din.readInt();
            }
            bin.close();
            din.close();
        } catch (Exception e){

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
