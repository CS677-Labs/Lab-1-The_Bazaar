# Lab-1-The_Bazaar
Peer to Peer system for an online bazaar

### Repo Structure
    - RPCServer has the server side RPC setup code
    - RPCClient has the client side RPC setup code
    - RPCServer has handlers for Lookup and Seller procedures that are classes in themselves.
    - For client node, we use the Lookup class from the entrypoint. Once you have the list of buyer ids, you call buy procedure in RPCClient class which executes the Seller class's sellProduct function on the server.
    - For server node, we run the RPCServer class from entrypoint

### Implementation details
    - Language: Java
    - Library: XML RPC

### Steps to run
    - Run the [file name] file using the command [command with args].
