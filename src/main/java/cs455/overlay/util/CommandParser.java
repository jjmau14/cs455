package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;
import java.util.Scanner;

public class CommandParser {

    public void registryParser(Registry registry) {
        Scanner scnr = new Scanner(System.in);
        while(true) {
            System.out.print("[" + Thread.currentThread().getName() + "] Enter a Registry Command: ");
            String cmd = scnr.nextLine();

            try {
                String[] cmdArray;
                switch (cmd.toLowerCase().split(" ")[0]) {
                    /**
                     * @Case: help
                     * Prints list of available commands.
                     * */
                    case "help":
                        System.out.println("list-messaging-nodes\t\tDisplays list of messaging nodes registered with the registry.");
                        System.out.println("setup-overlay [routing table size]\t\tSends routing manifest to messaging nodes.\n");
                        break;

                    /**
                     * @Case: list-messaging-nodes
                     * Lists Host ip address, port #, and Node GUID that are currently
                     * registered with the Registry.
                     * */
                    case "list-messaging-nodes":
                        registry.listMessagingNodes();
                        break;

                    /**
                     * @Case: list-routing-tables
                     *  Lists routing tables for each node in the overlay
                     * */
                    case "list-routing-tables":
                        registry.printManifests();
                        break;

                    case "start":
                        cmdArray = cmd.split(" ");
                        if (cmdArray.length != 2) {
                            System.out.println("USAGE: start [number of data packets]");
                            break;
                        }
                        registry.initDataStream(Integer.parseInt(cmdArray[1]));
                        break;

                    /**
                     * @Case: setup-overlay
                     * Takes one parameter argument that is the routing table size. This will determine
                     * the number of other nodes in each node's routing table.
                     * */
                    case "setup-overlay":
                        cmdArray = cmd.split(" ");
                        if (cmdArray.length != 2) {
                            System.out.println("USAGE: setup-overlay [routing table size]");
                            break;
                        }
                        registry.generateManifests(Integer.parseInt(cmdArray[1]));
                        break;

                    /**
                     * @Case: Empty String
                     * Prints new line for entering command rather than returning
                     * the default error "Unrecognized command"
                     * */
                    case "":
                        break;

                    /**
                     * @Case: Default
                     * Error: Command was not one of the available commands.
                     * */
                    default:
                        System.out.println("Unrecognized command \"" + cmd + "\". Try \"help\".");
                }
            } catch (Exception e){
                System.out.println("Error parsing command:" + e.getMessage());
            }
        }
    }

    public void messengerParser(MessagingNode messager){
        Scanner scnr = new Scanner(System.in);
        while(true) {
            System.out.print("[" + Thread.currentThread().getName() + "] Enter a Messenger Command: ");
            String cmd = scnr.nextLine();

            try {
                String[] cmdArray;
                switch (cmd.toLowerCase().split(" ")[0]) {
                    case "print-counters-and-diagnostics":
                        System.out.println("Printing diagnostics...");
                        messager.printDiagnostics();
                        break;

                    case "exit-overlay":
                        messager.exitOverlay();
                        System.out.println("Exiting overlay...");
                        break;
                    /**
                     * @Case: Help
                     * Prints list of available commands
                     * */
                    case "help":
                        break;

                    /**
                     * @Case: Empty String
                     * Prints new line for entering command rather than returning
                     * the default error "Unrecognized command"
                     * */
                    case "":
                        break;

                    /**
                     * @Case: Default
                     * Error: Command was not one of the available commands.
                     * */
                    default:
                        System.out.println("Unrecognized command \"" + cmd + "\". Try \"help\".");

                }
            } catch (Exception e){

            }
        }
    }

}
