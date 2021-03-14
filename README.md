# Lab 1 The Bazaar
Unstructured Peer to Peer system for online bazaar

**Team members**: Vignesh Radhakrishna (32577580, vradhakrishn@umass.edu), Adarsh Kolya (33018261, akolya@umass.edu), Brinda Murulidhara (32578418, bmurulidhara@umass.edu)

- Milestone 1 - https://github.com/CS677-Labs/Lab-1-The_Bazaar/tree/milestone1
- Milestone 2 - main branch
### Repo Structure
    - Server.java has the server side RMI setup code. 
    - Seller.java has the logic for the Seller functionality. We need to first create an interface for the remote object, which is defined in SellerNode.java. The Server implements this interface using Lookup and Seller classes.
    - Client.java has the client side code. This is basically getting command line arguments - nodeId and Path to the list of products for the buyer. The client then uses Lookup class and Buy class to make recurrrent requests to its neighbours with a random product everytime before sleeping for 2 seconds.
    - Lookup.java has the logic for the lookup functionality. // Todo: Elucidate more on this.
    - Buyer.java has the logic for the buyer functionality. // Todo: Elucidate more
    - config.properties stores ID as key and a comma separated list of properties as value. The first property is the URL and the remaining properties are IDs of neighboring nodes. This file is passed as an argument to both the buyer and seller nodes. Note: In order to create a custom network, the user must specify the neighbor IDs for each node in the config.properties file. The two configuration files in the repository contain the network definitions for the first 2 milestones.
    - Nodes.java stores the static list of all the peers in the network. (k neighbours are picked from these).
    - Reply.java has the reply message class.
    - SellerNode.java is the interface for the remote object.

### Generate config file for N nodes and K neighbors
    - User can utilize generateConfigFile.py script to generate the config file which can later be used to initiate buyer and seller nodes.
    - This script takes 3 inputs.
        1) Total number of nodes, N.
        2) Neighbors for each node, K.
        3) A driver file with key, value pairs which specify the URL of all the N nodes. The key here will be the node number and the value will be the URL.
           Example contents of the driver file is :
           0=http://127.0.0.1:5000
           1=http://127.0.0.1:5001
           2=http://127.0.0.1:5002
           3=http://127.0.0.1:5003
           4=http://127.0.0.1:5004
         
### Run testcases for Milestone 1
```shell
chmod 744 TestCasesMilestone1.sh
./TestCasesMilestone1.sh
```

### Run testcases for Milestone 2
```shell
chmod 744 TestCasesMilestone2.sh
./TestCasesMilestone2.sh
```

### Usage
Compile java files into bytecode
```shell
cd Lab-1-The_Bazaar
mkdir classfiles
javac -d classfiles src/*.java
```

#### Run seller (server)
```shell
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server {id of the seller} {path to the config.properties file} {list of products to buy separated by ,} {maximum number of items of a particular product type} {[optional] list of products to restock separated by ,}
```
Example
```shell
java -classpath classfiles -Djava.rmi.server.codebase=file:files/ Server 1 src/config.properties Fish, Boar 10 Fish
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
