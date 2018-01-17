package edu.csu.jjmau14.registry;

import edu.csu.jjmau14.util.ControlMessages;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry {

    private static String[] registry;

    public Registry(){
        this.registry = new String[128];
        try (
            ServerSocket ss = new ServerSocket(5000)
        ){
            while(true){
                // Wait for a client to connect
                Socket socket = ss.accept();

                // Initialize a new Data Input Stream to read data sent by the client
                DataInputStream dIn = new DataInputStream(socket.getInputStream());

                switch(dIn.read()) {
                    case ControlMessages.OVERLAY_NODE_SENDS_REGISTRATION:
                        int port = dIn.read();
                        break;
                    case ControlMessages.REGISTRY_REPORTS_REGISTRATION_STATUS:
                        break;
                }
            }
        }catch(Exception e){

        }
    }

    private int register(String ip) throws RegistrationException {
        int guid = -1;
        for (int i = 0 ; i < this.registry.length ; i++){
            if (this.registry[i].equals(ip)){
                throw new RegistrationException("IP already registered with guid: " + i);
            } else if (this.registry[i] == null ){
                guid = i;
            }
        }
        return guid;
    }

}
