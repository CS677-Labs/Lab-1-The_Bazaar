import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.UUID;

public class Lookup {
    Integer nodeId;
    String productName;
    private static ArrayList<String> processedLookups = new ArrayList<>();

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
     *
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
    */

    public ArrayList<Reply> lookup(String itemName, int maxHopCount) throws Exception {
        /*
         * Generate lookupId
         */
        String lookupId = UUID.randomUUID().toString();
        /*
         * Flood the lookup message
         */
        return floodLookUps(itemName, maxHopCount, lookupId);
    }

    public ArrayList<Reply> floodLookUps(String itemName, int maxHopCount, String lookupId) throws Exception {
        ArrayList<Reply> replies = new ArrayList<>();
        /*
         * 	Check if the current transaction has already been processed in this node
         */
        if(processedLookups.contains(lookupId))
            return replies;

        /*
         * 	Check if the item being sold matches to the one requested.
         * 	If yes, add the details of this node to "replies".
         */
        if (itemName.equals(this.productName)) {
            Reply currNode = new Reply(this.nodeId);
            replies.add(currNode);
        }
        else if(maxHopCount > 0){
            /*
             *  Fetch the neighbors and flood lookups to all the neighbors.
             */
            for (Integer ID : Nodes.neighbors) {
                URL url = new URL(Nodes.nodes.get(ID));
                System.out.println("Forwarding request to " + url.toString());
                try {
                    Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
                    SellerNode seller = (SellerNode) registry.lookup("SellerNode");
                    replies.addAll(seller.floodLookUps(itemName, maxHopCount-1, lookupId));
                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                }

            }
        }

        processedLookups.add(lookupId);
        return replies;
    }
}