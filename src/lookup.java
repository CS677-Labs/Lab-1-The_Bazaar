import java.util.ArrayList;

public class lookup {
	private Boolean isSeller = false;
	private String my_item;
	private int nodeId;
	
	
	
	public lookup (int ID)
	{
		nodeId = ID;
	}
	
	
	
	/*
	 * Function to fetch K neighbors for a node.
	 * Function will ensure that it returns only the nodes that haven't been visited.
	 */
	public ArrayList<Integer> GetKNeighbors()
	{
		return new ArrayList<Integer>();
	}
	
	
	
	public ArrayList<Reply> Lookup(String itemName, int maxHopCount)
	{
		ArrayList<Reply> replies = new ArrayList<Reply>();
		
		/*
		 * If this node is a seller :
		 * 		Check if the item being sold matches to the one being requested.
		 * 		If yes, add the details of this node to "replies".
		 */
		if(isSeller)
		{
			if(itemName.equals(my_item))
			{
				Reply currNode = new Reply(nodeId);
				replies.add(currNode);
			}
		}
		
		/*
		 * Fetch the neighbors and invoke lookup for all the neighbors.
		 */
		for (Integer ID : GetKNeighbors())
		{
			/*
			 * RPC -- To be implemented.
			 *
			RPCInterface rpc = new RPCInterface(ID);
			replies.addAll(rpc.invokeLookup());
			*/
		}
		
		return replies;
	}
	
	
	
	/*
	 * Setter method to set the item that the current node may be selling
	 */
	public void SetSellerItem(String item)
	{
		isSeller = true;
		my_item = item;
	}
	
	
	
	/*
	 * Method to indicate that the current node will not be a seller anymore.
	 */
	public void UnsetSellerItem()
	{
		this.isSeller = false;
		my_item = "";
	}
}
