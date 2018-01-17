package edu.csu.jjmau14.registry;

import edu.csu.jjmau14.util.ControlMessages;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry {

    private static int[] registry;

    public Registry(){
        this.registry = new int[128];
        try (
            ServerSocket ss = new ServerSocket(5000)
        ){
            while(true){
                // Wait for a client to connect
                Socket socket = ss.accept();

                // Initialize a new Data Input Stream to read data sent by the client
                DataInputStream dIn = new DataInputStream(socket.getInputStream());

                switch(dIn.read()){
                    case ControlMessages.OVERLAY_NODE_SENDS_REGISTRATION:
                        break;
                    case ControlMessages.REGISTRY_REPORTS_REGISTRATION_STATUS:
                        break;
                }
            }
        }catch(Exception e){

        }
    }

}
