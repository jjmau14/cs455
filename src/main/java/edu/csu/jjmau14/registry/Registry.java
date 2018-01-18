package edu.csu.jjmau14.registry;

import edu.csu.jjmau14.util.ControlMessages;
import edu.csu.jjmau14.util.RegisteredMessenger;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry {

    private static RegisteredMessenger[] registry;

    public Registry(){
        this.registry = new RegisteredMessenger[128];
        try (
            ServerSocket ss = new ServerSocket(5000)
        ){
            while(true){
                // Wait for a client to connect
                Socket socket = ss.accept();

                // Initialize a new Data Input Stream to read data sent by the client
                DataInputStream dIn = new DataInputStream(socket.getInputStream());

                switch(dIn.readByte()) {
                    case ControlMessages.OVERLAY_NODE_SENDS_REGISTRATION:
                        int ipLength = dIn.readByte();
                        byte[] ipBytes = new byte[ipLength];
                        for (int i = 0 ; i < ipLength ; i++){
                            ipBytes[i] = dIn.readByte();
                        }
                        int port = dIn.read();
                        register(new RegisteredMessenger(ipBytes, port));
                        break;
                    case ControlMessages.REGISTRY_REPORTS_REGISTRATION_STATUS:
                        break;
                }
            }
        }catch(Exception e){

        }
    }

    private int register(RegisteredMessenger rm) throws RegistrationException {
        for (int i = 0 ; i < this.registry.length ; i++){
            if (this.registry[i].equals(rm)){
                return -1;
            } else if (this.registry[i] == null ){
                this.registry[i] = rm;
                return i;
            }
        }
        return -1;
    }

}
