package cs455.overlay.util;

import cs455.overlay.routing.RegisterItem;
import cs455.overlay.node.Registry;
import dnl.utils.text.table.TextTable;

import java.util.Hashtable;
import java.util.Scanner;

public class CommandParser {

    public void registryParser(){
        Scanner scnr = new Scanner(System.in);
        while(true) {
            System.out.print("[" + Thread.currentThread().getName() + "] Enter a Registry Command: ");
            String cmd = scnr.nextLine();

            try {
                switch (cmd.toLowerCase().split(" ")[0]) {
                    case "help":
                        System.out.println("list-messaging-nodes\t\tDisplays list of messaging nodes registered with the registry.");
                        System.out.println("setup-overlay [routing table size]\t\tSends routing manifest to messaging nodes.");
                        System.out.println();
                        break;
                    case "":
                        break;
                    case "list-messaging-nodes":
                        Hashtable<Integer, RegisterItem> registry = Registry.getRegistry();
                        System.out.println(registry.size());
                        String[][] data = new String[Registry.getSize()][3];
                        for (int i = 0 ; i < registry.size() ; i++) {
                            data[i][0] = registry.get(i).ipToString();
                            data[i][1] = Integer.toString(registry.get(i).getPort());
                            data[i][2] = Integer.toString(registry.get(i).getId());

                        }
                        System.out.println("There " + (Registry.getSize() == 1 ? "is 1 node registered with the registry." :
                                "are " + Registry.getSize() + " nodes registered with the registry."));
                        TextTable tt = new TextTable(new String[]{"Host", "Port", "Node ID"}, data);
                        tt.printTable();
                        System.out.println();
                        break;
                    case "setup-overlay":
                        String[] cmdArray = cmd.split(" ");
                        if (cmdArray.length != 2) {
                            System.out.println("USAGE: setup-overlay [routing table size]");
                            break;
                        }
                        Registry.generateManifests(Integer.parseInt(cmdArray[1]));
                        break;
                    default:
                        System.out.println("Unrecognized command \"" + cmd + "\". Try \"help\".");
                }
            } catch (Exception e){
                System.out.println("Error parsing command.");
            }
        }
    }

}
