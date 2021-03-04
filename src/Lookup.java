import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Lookup {
    Integer nodeId;
    String productName;

    public Lookup(Integer ID, String productName) {
        this.nodeId = ID;
        this.productName = productName;
    }


    /*
     * Function to fetch K neighbors for a node.
     * Function will ensure that it returns only the nodes that haven't been visited.
     *
     * Current implementation assumes only N=2 and K=1.
     * To be enhanced after Milestone 1
     */
    public ArrayList<Integer> GetKNeighbors() {
        ArrayList<Integer> neighbors = new ArrayList<>();
        Integer neighborId;
        // Todo: handle k neighbours criteria. How do you select k neighbours for unstructured peer to peer system?
        if(this.nodeId == 1)
            neighborId = 2;
        else
            neighborId = 1;
        neighbors.add(neighborId);
        return neighbors;
    }

    public ArrayList<Reply> lookup(String itemName, int maxHopCount) throws Exception {
        ArrayList<Reply> replies = new ArrayList<>();

        /*
         * 	Check if the item being sold matches to the one requested.
         * 	If yes, add the details of this node to "replies".
         */
        if (itemName.equals(this.productName)) {
            Reply currNode = new Reply(this.nodeId);
            replies.add(currNode);

        }
        else if(this.productName.isEmpty() && maxHopCount > 0){
            /*
             *  Fetch the neighbors and invoke lookup for all the neighbors.
             *
             *
             *
             *  Look for K neighbors only if the current node is not a seller node.
             *  If the current node is a seller node (productName is not empty) do not forward the lookup requests.
             *
             *  Works only for N=2, K=1 case. Needs to be enhanced to handle advanced cases.
             */
            for (Integer ID : GetKNeighbors()) {
                URL url = new URL(Nodes.nodes.get(ID));

                try {
                    Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
                    SellerNode seller = (SellerNode) registry.lookup("SellerNode");
                    replies.addAll(seller.lookUp(itemName, maxHopCount-1));
                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                    throw e;
                }

            }
        }
        return replies;
    }
}