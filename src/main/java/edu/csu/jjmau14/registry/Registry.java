package edu.csu.jjmau14.registry;

import edu.csu.jjmau14.util.ControlMessages;
import edu.csu.jjmau14.util.RegisteredMessenger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
                while(true) {
                    // Initialize a new Data Input Stream to read data sent by the client
                    DataInputStream dIn = new DataInputStream(socket.getInputStream());

                    switch (dIn.readByte()) {
                        case ControlMessages.OVERLAY_NODE_SENDS_REGISTRATION:

                            // Gather data inputs from socket
                            int ipLength = dIn.readByte();
                            byte[] ipBytes = new byte[ipLength];
                            for (int i = 0; i < ipLength; i++) {
                                ipBytes[i] = dIn.readByte();
                            }
                            int port = dIn.read();

                            // Register IP or error if already registered.
                            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

                            String statusText;
                            int status = -1;

                            // if (socket ip != requested register Ip)
                            try {
                                status = register(new RegisteredMessenger(ipBytes, port));
                                statusText = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + this.countRegistered + ")";

                            } catch (RegistrationException re) {
                                statusText = "Error registering IP: " + re.getMessage();
                            }

                            // Send reply to messaging node.
                            dOut.writeByte(ControlMessages.REGISTRY_REPORTS_REGISTRATION_STATUS);
                            dOut.writeByte(status);
                            dOut.writeByte(statusText.getBytes().length);
                            dOut.write(statusText.getBytes());

                            break;
                        case ControlMessages.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                            System.out.println((this.registry[dIn.read()].toString() + " :: " + (socket.getInetAddress())));
                            int len2 = dIn.readByte();
                            byte[] x = new byte[len2];
                            for (int i = 0; i < len2; i++) {
                                x[i] = dIn.readByte();
                            }
                            String m = new String(x);
                            System.out.println(m);
                            break;
                    }
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
                throw new RegistrationException("IP already registered with registry.");
            }
        }
        throw new RegistrationException("Registry full!");
    }

}
