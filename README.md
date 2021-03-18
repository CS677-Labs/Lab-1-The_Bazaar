# Lab 1 The Bazaar
Unstructured Peer to Peer system for online bazaar

**Team members**: Vignesh Radhakrishna (32577580, vradhakrishn@umass.edu), Adarsh Kolya (33018261, akolya@umass.edu), Brinda Murulidhara (32578418, bmurulidhara@umass.edu)

- Milestone 1 - https://github.com/CS677-Labs/Lab-1-The_Bazaar/tree/milestone1
- Milestone 2 - https://github.com/CS677-Labs/Lab-1-The_Bazaar/tree/milestone2
- Milestone 3 - https://github.com/CS677-Labs/Lab-1-The_Bazaar/tree/milestone3

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

### Assumption
Java and Git are installed on all servers.  
When used in multi-server mode, all servers will be AWS EC2 instances with passwordless SSH enabled for ec2-user.

### Run testcases for Milestone 1
```shell
chmod 744 tests/TestCasesMilestone1.sh
. tests/TestCasesMilestone1.sh
```

### Run testcases for Milestone 2
```shell
chmod 744 tests/TestCasesMilestone2.sh
. tests/TestCasesMilestone2.sh
```

### Run testcases for Milestone 3
```shell
chmod 744 tests/TestCasesMilestone3.sh
. tests/TestCasesMilestone3.sh
```

### Run the system for an arbitrary N and K on multiple servers.
Use the run.sh in the base directory to run the system for an arbitrary N and K.
The system will randomly assign K neighbors to every node and randomly assign buyer/seller roles to peers.

Syntax :
```shell
run.sh <N> <K> <Driver_file>
```

Example :
```shell
run.sh 10 4 DRIVER
```
Driver file should be placed in the base directory.  
Driver file here will contain the IPs of the machines where N nodes should be invoked. IPs are separated by lines.  
The system will distribute N nodes uniformly across these machines. 

Example:
```shell
ec2-34-229-161-96.compute-1.amazonaws.com
ec2-52-3-242-196.compute-1.amazonaws.com
```







### To run custom use-cases
If the system needs to be run on a user defined or if user needs to control role assignment, please refer to the below documentation.

Compile java files into bytecode and generate the jar files for Server and Client
```shell
cd Lab-1-The_Bazaar
mkdir build
cp -r generateConfigFile.py build && cd build
javac -d classfiles ../src/*.java
jar cfe Server.jar Server -C classfiles . && jar cfe Client.jar Client -C classfiles .
```

### Generate config file for N nodes and K neighbors

    - User can utilize generateConfigFile.py script to generate the config file which can later be used to initiate buyer and seller nodes.
    - This script will take the values of N, K and a driver-file, and config-file as input in the command line args

    Usage - python generateConfigFile.py <N> <K> <driver-path> <config-path>
    Example - python generateConfigFile.py 10 4 /tmp/driverFile /tmp/config.properties
   
    Params :
       N - Number of nodes
       K - Number of neighbors per node
       driver-file -   Full path of the driver file.
                       The driver file is a file with the IPs of the machines to be used as input
                       Will write the generated output to a config file
                       Example contents of a river file :
                       
                       http://127.0.0.1
                       http://172.2.3.4
                       http://172.123.323.23
                       

       config-file - Full path to the config file that is to be generated

    Sample output for N=5 and K=2 with the above driver file.
    
    0=http://127.0.0.1:5000,1,4
    1=http://172.2.3.4:5001,2,0
    2=http://172.123.323.23:5002,3,1
    3=http://127.0.0.1:5003,4,2
    4=http://172.2.3.4:5004,0,3

Use the config file generated while invoking each of these nodes.

#### Run seller (server)
```shell
java -Djava.rmi.server.codebase=file:files/ -jar Server.jar {id of the seller} {path to the config.properties file} {list of products to buy separated by ,} {maximum number of items of a particular product type} {[optional] list of products to restock separated by ,}
```
Example
```shell
java -Djava.rmi.server.codebase=file:files/ -jar Server.jar 1 src/config.properties Fish, Boar 10 Fish
```

#### Run buyer (client)
```shell
java -Djava.rmi.server.codebase=file:files/ -jar Client.jar {id of the buyer} {path to the config.properties file} {list of products to buy separated by ,}
```

Example
Run buyer (client)
```shell
java -Djava.rmi.server.codebase=file:files/ -jar Client.jar 1 src/config.properties Fish
```
