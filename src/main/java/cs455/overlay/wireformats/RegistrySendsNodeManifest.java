package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingTable;

public class RegistrySendsNodeManifest {

    byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    RoutingTable routes;

    public RegistrySendsNodeManifest(RoutingTable routes){
        this.routes = routes;
    }

    public byte[] pack(){
        byte[] data = new byte[10000];

        data[0] = this.type;
        data[1] = routes.tableSize();
    }

    public void craft(byte[] data){

    }

}
