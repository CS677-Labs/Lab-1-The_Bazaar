# TestCasesMilestone1.sh
##Configuration
Number of peers (N): 2\
Number of neighbors (k): 1\
Number of machines: 1\
Roles: Buyer or Seller

### Test Case 1
##### Description: 
One peer is a buyer of fish and another is a seller of fish. The seller initially has 5 fish to sell. The buyer buys fish from the seller one after another with a gap of 2 seconds. 
The seller restocks fish when the last fish is sold and thus ensures that there is always a stock of fish to sell.
The test case ends after an arbitrary interval of time when the client and server processes are terminated programmatically.
##### Results:
The results of the test case show that the buyer continuously buys fish while the seller restocks when the last fish is sold. Hence, the seller never runs out of fish to sell to the buyer.

#### Test Case 2
##### Description:
One peer is a buyer of fish and another is a seller of boar. The seller initially has 5 boars to sell.
The test case ends after an arbitrary interval of time when the client and server processes are terminated programmatically.

##### Results:
Nothing is sold as the product required by the buyer is not sold by any seller.

#### Test Case 3
##### Description:
Buyer and seller roles are assigned randomly. This is done by picking a random node id between 1 and 2 and assigning it the role of a seller. The other node is assigned the role of a buyer. 
The seller initially has 5 boars to sell. The seller restocks boars when the last boar is sold and thus ensures that there is always a stock of boars to sell.
The test case ends after an arbitrary interval of time when the client and server processes are terminated programmatically.

##### Results:
The results of the test case show that the buyer continuously buys boars while the seller restocks when the last boar is sold. Hence, the seller never runs out of boars to sell to the buyer.

# TestCasesMilestone2.sh
##Configuration
Number of peers (N): 5\
Number of neighbors (k): 4\
Number of machines: 1\
Roles: Buyer or Seller or None (No role indicates that the buyer/seller peer is not involved in buying or selling in the current transaction. However, it forwards lookup requests to its neighbors.)\
Network: The peer-to-peer overlay network is created such that the buyers and sellers are 2-hops away.

#### Test Case 1
##### Description:
One peer is a seller of boars and three peers are buyers of boars, the remaining peer has no role in selling/buying boars as it is a seller of fish.
The seller of boars initially has 5 boars and the seller of fish initially has 1 fish.
The test case ends after an arbitrary interval of time when the client and server processes are terminated programmatically.

##### Results:
The results of the test case show that the buyers continuously buy boars while the seller restocks when the last boar is sold. Hence, the seller never runs out of boars to sell to the buyers.

#### Test Case 2
##### Description:
One peer is a seller of boars and three peers are buyers of boars, the remaining peer has no role in selling/buying boars as it is a seller of fish.
The seller of boars initially has 1 boar, and the seller of fish initially has 1 fish. The seller of boars starts selling fish after the boar is sold.
The test case ends after an arbitrary interval of time when the client and server processes are terminated programmatically.

##### Results:
As the seller of boar has only 1 boar to sell, and it restocks a different product (fish) after the boar is sold, only one buyer gets to buy the boar even though both buyers request for the same product. Note: The two buyers receive the id of the seller of boars on a look-up. However, only one of the buyers is able to buy the boar when they attempt to buy.

# TestCasesMilestone3.sh
##Configuration
Number of peers (N): 3\
Number of neighbors (k): 2\
Network: The peer-to-peer overlay network is created by assigning random k neighbors to every node.

##### Description:
A list of machine IPs are taken as input in a file. If one of the machines is localhost, the entire p2p system would run on localhost. Otherwise, the jar files are copied to the remote machine, and the script is executed on all nodes. The remote logs are used to validate the outputs. 
