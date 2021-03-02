import java.util.ArrayList;
import org.apache.xmlrpc.XmlRpcException;

public class Lookup {
	/*
	 * Function to fetch K neighbors for a node.
	 * Function will ensure that it returns only the nodes that haven't been visited.
	 */

	public ArrayList<Integer> GetKNeighbors() {
		return new ArrayList<Integer>();
	}

	public ArrayList<Reply> lookup(String itemName, int maxHopCount) throws XmlRpcException {
		ArrayList<Reply> replies = new ArrayList<Reply>();

		/*
		 * 	Check if the item being sold matches to the one requested.
		 * 	If yes, add the details of this node to "replies".
		 */

		if (itemName.equals(RPCServer.productName)) {
			Reply currNode = new Reply(RPCServer.ID);
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
