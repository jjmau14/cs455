package cs455.overlay.wireformats;

public class ReportNodeCrashToRegistry extends Event {

    int type = 100;
    byte[] data;

    public ReportNodeCrashToRegistry(byte[] b){
        this.data = b;
    }

    public byte[] getErr(){
        return this.data;
    }

    @Override
    public int getType(){
        return this.type;
    }

    @Override
    public byte[] pack(){
        return null;
    }

    @Override
    public void craft(byte[] b){

    }



}
