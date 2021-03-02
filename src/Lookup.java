import java.util.ArrayList;
import org.apache.xmlrpc.XmlRpcException;

public class Lookup {
	Integer nodeId;

	/*
	 * WARNING :
	 * Default constructor to be called only on the RPCServer.
	 */
	public Lookup() {
		nodeId = RPCServer.ID;
	}

	public Lookup(Integer ID) {
		nodeId = ID;
	}


	/*
	 * Function to fetch K neighbors for a node.
	 * Function will ensure that it returns only the nodes that haven't been visited.
	 *
	 * Current implementation assumes only N=2 and K=1.
	 * To be enhanced after Milestone 1
	 */
	public ArrayList<Integer> GetKNeighbors() {
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		Integer neighborId;
		if(nodeId == 0)
			neighborId = 1;
		else
			neighborId = 0;

		neighbors.add(neighborId);
		return neighbors;
	}

	public ArrayList<Reply> lookup(String itemName, int maxHopCount) throws XmlRpcException {
		ArrayList<Reply> replies = new ArrayList<Reply>();

		/*
		 * 	Check if the item being sold matches to the one requested.
		 * 	If yes, add the details of this node to "replies".
		 */
		if (itemName.equals(RPCServer.productName)) {
			Reply currNode = new Reply(nodeId);
			replies.add(currNode);
		}
		else if(maxHopCount > 0){
			/*
			 * Fetch the neighbors and invoke lookup for all the neighbors.
			 */
			for (Integer ID : GetKNeighbors()) {
				RPCClient neighbor = new RPCClient(ID);
				replies.addAll(neighbor.lookUp(itemName, maxHopCount - 1));
			}
		}
		return replies;
	}
}
