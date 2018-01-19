package edu.csu.jjmau14.registry;

import dnl.utils.text.table.TextTable;
import edu.csu.jjmau14.util.ControlMessages;
import edu.csu.jjmau14.util.RegisteredMessenger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Scanner;

public class Registry {

    private static RegisteredMessenger[] registry;
    private static int countRegistered;

    public Registry(){
        this.registry = new RegisteredMessenger[128];
        this.countRegistered = 0;
        new Thread(() -> {
            try (
                    ServerSocket ss = new ServerSocket(5000)
            ) {
                while (true) {
                    // Wait for a client to connect
                    Socket socket = ss.accept();
                    boolean exit = false;
                    while (!exit) {
                        // Initialize a new Data Input Stream to read data sent by the client
                        DataInputStream dIn = new DataInputStream(socket.getInputStream());

                        switch (dIn.readByte()) {
                            case ControlMessages.OVERLAY_NODE_SENDS_REGISTRATION:{

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
                                    exit = true;
                                }

                                // Send reply to messaging node.
                                dOut.writeByte(ControlMessages.REGISTRY_REPORTS_REGISTRATION_STATUS);
                                dOut.writeByte(status);
                                dOut.writeByte(statusText.getBytes().length);
                                dOut.write(statusText.getBytes());

                                break;
                            }

                            case ControlMessages.NODE_REPORTS_OVERLAY_SETUP_STATUS: {
                                int length = dIn.readByte();
                                byte[] message = new byte[length];
                                for (int i = 0; i < length; i++) {
                                    message[i] = dIn.readByte();
                                }
                                System.out.println("[" + Thread.currentThread().getName() + "]" + new String(message));
                                exit = true;
                                break;
                            }
                        }
                    }
                    socket.close();
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }).start();
        new Thread(() -> {
            Scanner scnr = new Scanner(System.in);
            while(true){
                System.out.print("[" + Thread.currentThread().getName() + "] Enter a command: ");
                String cmd = scnr.nextLine();
                switch (cmd) {
                    case "list-messaging-nodes":
                        System.out.println(this.countRegistered + (this.countRegistered == 1 ? " node is registered:" : " nodes are registered."));
                        String[] cols = new String[]{"Hostname", "Port", "Node ID"};
                        String[][] data = new String[this.countRegistered][3];
                        int count = 0;
                        for (int i = 0; i < this.registry.length; i++) {
                            if (this.registry[i] != null){
                                data[count][0] = this.registry[i].getIpString();
                                data[count][1] = Integer.toString(this.registry[i].getPort());
                                data[count][2] = Integer.toString(this.registry[i].getId());
                                count++;
                            }
                        }
                        TextTable tt = new TextTable(cols, data);
                        tt.printTable();
                        System.out.println();
                        break;
                    case "help":
                        System.out.println("\nCommands:");
                        System.out.println("list-messaging-nodes\t-\tList all nodes registered in the overlay.\n");
                        break;
                    default:
                        System.out.println("Invalid command, try typing \"help\".\n");
                }
            }

        }).start();
    }

    private int register(RegisteredMessenger rm) throws RegistrationException {
        for (int i = 0 ; i < this.registry.length ; i++){
            if (this.registry[i] == null){
                this.registry[i] = rm;
                this.countRegistered += 1;
                rm.setId(i);
                return i;
            } else if (this.registry[i].equals(rm)){
                throw new RegistrationException("IP already registered with registry.");
            }
        }
        throw new RegistrationException("Registry full!");
    }

}
