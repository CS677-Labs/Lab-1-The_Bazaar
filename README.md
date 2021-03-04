# Lab 1 The Bazaar
Unstructured Peer to Peer system for online bazaar

Team members: Vignesh Radhakrishna (vradhakrishn@umass.edu), Adarsh Kolya (akolya@umass.edu), Brinda Muralidhara (bmurulidhara@umass.edu)

### Repo Structure
    - Server.java has the server side RMI setup code. 
    - Seller.java has the logic for the Seller functionality. We need to first create an interface for the remote object, which is defined in SellerNode.java. The Server implements this interface using Lookup and Seller classes.
    - Client.java has the client side code. This is basically getting command line arguments - nodeId and Path to the list of products for the buyer. The client then uses Lookup class and Buy class to make recurrrent requests to its neighbours with a random product everytime before sleeping for 2 seconds.
    - Lookup.java has the logic for the lookup functionality. // Todo: Elucidate more on this.
    - Buyer.java has the logic for the buyer functionality. // Todo: Elucidate more
    - config.properties stores the mapping from ID and URL. This is passed as an argument to both the buyer and seller nodes.
    - Nodes.java stores the static list of all the peers in the network. (k neighbours are picked from these).
    - Reply.java has the reply message class.
    - SellerNode.java is the interface for the remote object.

### Usage
Compile java files into bytecode
```shell
cd Lab-1-The_Bazaar
mkdir classfiles
java -d classfiles src/*.java
```

#### Run seller (server)
```shell
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server {id of the seller} {path to the config.properties file} {list of products to buy separated by ,}
```
Example
```shell
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 1 src/config.properties Fish, Boar
```

#### Run buyer (client)
```shell
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client {id of the buyer} {path to the config.properties file} {list of products to buy separated by ,}
```

Example
Run buyer (client)
```shell
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Client 1 src/config.properties Fish
```

### Run testcases for Milestone 1
```shell
chmod 744 test_cases.sh
./test_cases.sh
```
