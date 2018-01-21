package cs455.overlay.node;

import cs455.overlay.routing.RegisterItem;
import cs455.overlay.transport.TCPConnection.TCPReceiver;
import cs455.overlay.transport.TCPConnection.TCPSender;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Registry extends Node{

    private static RegisterItem[] registry = new RegisterItem[128];
    private static int registerCount = 0;
    private static ServerSocket server;

    public static void main(String[] args) throws Exception {
        if (args.length != 1){
            System.out.println("USAGE: java cs455.overlay.node.Registry [Port Number]");
            System.exit(1);
        }

        server = new ServerSocket(5000);
        System.out.println("Registry running on " + InetAddress.getLocalHost().getHostAddress() + ":" + server.getLocalPort() + "...");
        cycle();
    }

    private static void cycle(){
        try {

            while(true){
                Socket socket = server.accept();

                byte[] data = new TCPReceiver(socket).read();

                switch (data[0]){
                    case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                        OverlayNodeSendsRegistration ONSR = new OverlayNodeSendsRegistration();
                        ONSR.craft(data);

                        int id = -1;
                        String message = "";

                        try {
                            id = register(new RegisterItem(ONSR.getIp(), ONSR.getPort()));
                            message = "Registration request successful. There are currently (" + registerCount + ") nodes constituting the overlay.";
                        } catch (Exception e){
                            message = e.getMessage();
                        }

                        RegistryReportsRegistrationStatus RRRS = new RegistryReportsRegistrationStatus(id, message);
                        new Thread(() -> {
                            try {
                                new TCPSender(socket).sendData(RRRS.pack());
                            } catch (Exception e){

                            }
                        }).start();
                }
            }
        } catch (Exception e){
            System.out.println("[Registry - " + Thread.currentThread().getName() + "]: Error in server thread: " + e.getMessage());
            System.exit(1);
        }
    }

    private static int register(RegisterItem ri) throws Exception {
        int index = -1;
        for (int i = 0 ; i < registry.length ; i++){
            if (registry[i] == null && index == -1){
                index = i;
            } else if (registry[i] != null && registry[i].equals(ri)){
                throw new Exception("This IP has already been registered with the registry.");
            }
        }
        if (index == -1)
            throw new Exception("Registry is full.");
        registry[index] = ri;
        registerCount += 1;
        return index;
    }

}
