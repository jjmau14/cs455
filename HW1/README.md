# CS455 - PC1: Overlay Network
#### Josh Mau

### Configuring the Registry
* `java cs455.overlay.node.Registry [Port Number]` - must provide a port for the Registry to run on. This port will be required to start all messenger nodes.

### Configuring Messenger nodes
* `java cs455.overlay.node.MessagingNode [IP of Registry] [Registry Port Number]` - Messenger nodes will automatically register themselves with the Registry when they come on line.

### Building the overlay network
* From the Registry, use the following command: `setup-overlay [Size of Routing Table]`
The size of the routing table will determine how many cached TCP connections each node will have.

### Registry Commands
* `list-messaging-nodes`: displays all registered nodes ip, port, and overlay unique ID.
* `setup-overlay [Sze of Routing Table]`: creates overlay network and sends routing tables to each node.
* `list-routing-tables`: displays all routing tables for each node.
* `start [Rounds]`: execute overlay work routine 'Rounds' many times.
