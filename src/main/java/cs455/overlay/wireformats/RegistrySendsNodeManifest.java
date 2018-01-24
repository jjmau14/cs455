package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingTable;

public class RegistrySendsNodeManifest {

    byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    RoutingTable routes;

    public RegistrySendsNodeManifest(RoutingTable routes){
        this.routes = routes;
    }

    public byte[] pack() throws Exception {
        try {
            byte tableSize = routes.getTableSize();
            byte ipSize = (byte) routes.getRoute(0).getIp().length;
            byte[] data = new byte[1+1+(tableSize * (9+ipSize)) + 5];


            data[0] = this.type;
            data[1] = routes.getTableSize();
        } catch (IndexOutOfBoundsException ioobe){
            throw new IndexOutOfBoundsException("Routing table may be empty: " + ioobe.getMessage());
        }
        return null;
    }

    public void craft(byte[] data){

    }

}
