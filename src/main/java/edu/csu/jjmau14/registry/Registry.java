package edu.csu.jjmau14.registry;

import edu.csu.jjmau14.util.ControlMessages;
import edu.csu.jjmau14.util.RegisteredMessenger;

import javax.naming.ldap.Control;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Registry {

    private static RegisteredMessenger[] registry;
    private static int countRegistered;

    public Registry(){
        this.registry = new RegisteredMessenger[128];
        this.countRegistered = 0;
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

                        // Gather data inputs from socket
                        int ipLength = dIn.readByte();
                        byte[] ipBytes = new byte[ipLength];
                        for (int i = 0 ; i < ipLength ; i++){
                            ipBytes[i] = dIn.readByte();
                        }
                        int port = dIn.read();

                        // Register IP or error if already registered.
                        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                        int status = register(new RegisteredMessenger(ipBytes, port));

                        // Send reply to messaging node.
                        dOut.writeByte(ControlMessages.REGISTRY_REPORTS_REGISTRATION_STATUS);
                        dOut.writeByte(status);
                        String message = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + this.countRegistered + ")";
                        dOut.writeByte(message.getBytes().length);
                        dOut.write(message.getBytes());
                        
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
            if (this.registry[i] == null){
                this.registry[i] = rm;
                this.countRegistered += 1;
                return i;
            } else if (this.registry[i].equals(rm)){
                return -1;
            }
        }
        return -1;
    }

}
