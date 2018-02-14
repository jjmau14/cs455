CS455 - PC1: Overlay Network
Josh Mau

abstract: Classes that will create an overlay network with interconnected nodes that can send and receive messages
          between each other and route packets to the proper sink. Also contains functionality to collect statistics
          for each task executed on the network.

==================================================================================================

Files included:
  * Source code: contains all *.java files for this program.
  * Makefile: provides `make clean` and `make all` functionality.
  * This README.txt containing information about the code.

==================================================================================================

How to run the code:
  * Use command `make all` to compile all the java code into class files.
  * First, remote into a host machine and start the Registry with the following command: `java cs455.overlay.node.Registry [enter any port]`
    If any exception is thrown, try a different port, that one may already be in use
  * Use the following command on any number of remote hosts (or the same host as the registry or multiple times on any host)
    `java cs455.overlay.node.MessagingNode [RegistryHost Name or IP] [Registry Port from above]`
  * Proceed to the configuration section (Step 3) below to setup an overlay and run tasks.

==================================================================================================

Configuration:
  Configuring the Registry
    1.) `java cs455.overlay.node.Registry [Port Number]` - must provide a port for the Registry to run on. This port will be required to start all messenger nodes.

  Configuring Messenger nodes
    2.) `java cs455.overlay.node.MessagingNode [IP of Registry] [Registry Port Number]` - Messenger nodes will automatically register themselves with the Registry when they come on line.

  Building the overlay network
    3.) After all the nodes are registered, on the Registry node use the following command: `setup-overlay [Size of Routing Table]`
      The size of the routing table will determine how many cached TCP connections each node will have.

  Execute a task
    4.) Use the following command from the Registry to send `x` number of messaging from each node:
      `start [x number of messages]`. When every node has completed its task and there are no more packets
      routing the overlay, task summary statistics will automatically be displayed. If you ever see something
      similar to "259995/260000 Packets did not match. Resending request for summary..." this means the summary
      results were sent too early and there are still packets routing the overlay. The registry will wait a short
      amount of time before requesting summary stats again from each nodes. Generally this will not occur unless
      machines are under heavy load from other tasks.

==================================================================================================

Commands:
  Registry Commands
    * `list-messaging-nodes`: displays all registered nodes ip, port, and overlay unique ID.
    * `setup-overlay [Sze of Routing Table]`: creates overlay network and sends routing tables to each node.
    * `list-routing-tables`: displays all routing tables for each node.
    * `start [Rounds]`: execute overlay work routine 'Rounds' many times.

  MessagingNode Commands
    * `print-counters-and-diagnostics`: displays all current results of the running or previously ran task such as
      messages sent/received/relayed and current sum received and sum sent.
    * `exit-overlay`: requests removal from the overlay by the registry. Upon approval, the node will exit the overlay
      and terminate the process.

==================================================================================================

Packages:
  * cs455.overlay
    * node - contains types of nodes (Registry and Messaging)
      Classes:
      * node (abstract): contains required onEvent method that must be implemented by the Registry and MessaingNode

      * Registry: Registry nodes that generate unique identifiers across the overlay network for sending messages between
        nodes, handles building an overlay network from nodes that have registered, starting tasks and gathering
        summary data from finished tasks.

      * MessagingNode: Individual messaging nodes that register with the registry and become part of the overlay network.
        These nodes participate in sending, receiving, and forwarding messages, as well as tracking summary information
        for each task requested by the registry.

    * routing - contains classes for holding lists of routes used by each node to send messages
      Classes:
      * RegisterItem: Used to store information about each node that has registered including their unique identifiers,
      task completion statuses, and whether or not the registry has received the summary from that node.

      * Route: Holds ip address, port, and unique identifier for any given route (messaging node or registry)

      * RoutingTable: Contains a list of routes created by the registry when creating the overlay network. These routing
        tables are used to create the overlay when each node opens connections to each route in its given routing table.

    * transport - contains classes for handling and managing incoming and outgoing network traffic
      Classes:
      * TCPConnection: Holds socket connections as well as methods to initialize a sender and receiver thread for that
        socket. This class handles sending data to the Event Factory when data has been received.

      * TCPConnectionCache: Holds a list of TCPConnection objects that any given node wants to maintain contact with.
        This class is used when sending data to get the nearest ID of the node the data is being sent to.

      * TCPServerThread: Initializes a ServerSocket and creates a new TCPConnection with the socket returned by .accept().

    * util - contains classes for interacting with the overlay (command parser)
      Classes:
      * CommandParser: Contains command parser functions to handle user input while the overlay is running. Handles all
        commands specified above in the configuration section.

    * wireformats - contains classes for parsing byte streams into usable objects and packaging them back
                    up into byte arrays to send back over the wire.
      Classes:
      * Event (abstract): contains methods all wireformats must implement (pack/craft) to handle sending or receiving
        data in wireformat form.

      * EventFactory: parses incoming byte arrays into objects based on their type and casts them into the proper wireformat
        and calls the node.onEvent method.

      * Protocol (interface): contains final integer values that will determine which type of wireformat the event factory
        should parse the byte array into.

      * NodeReportsOverlaySetupStatus: used to send the registry an acknowledgement that the node has successfully configured
        its overlay connections.

      * OverlayNodeReportsTaskFinished: used to send the registry an acknowledgement that the nodes has completed sending
        all messages requested by the registry from a task initiate request.

      * OverlayNodeReportsTrafficSummary: sends summary of messages sent, received, and relayed as well as sum of messages
        sent and received.

      * OverlayNodeSendsData: used during the task execution to parse payload from MessagingNode to MessagingNode communication.

      * OverlayNodeSendsDeregistration: sends the registry a request for the MessagingNode to be deregistered from the overlay.

      * OverlayNodeSendsRegistration: when a MessagingNode is activated, this wireformate is used to request registration,
        and to let the registry know when port all other nodes should use to build the overlay.

      * RegistryReportsDeregistrationStatus: used to acknowledge that a MessagingNode is ok to remove itself from the overlay.

      * RegistryReportsRegistrationStatus: used to send an acknowledgement to a MessagingNode that it has been registered
        with the overlay and to set the nodes unique identifier.

      * RegistryRequestsTaskInitiate: used in the `start` commands to initiate a task on the overlay.

      * RegistryRequestsTrafficSummary: notification to all MessagingNodes that they should report the current traffic
        summary to the registry.

      * RegistrySendsNodeManifest: Contains a routing table that each MessagingNode should open and maintain connections
        with that comprise the overlay network.
