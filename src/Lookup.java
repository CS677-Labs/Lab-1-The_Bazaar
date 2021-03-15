import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

public class Lookup {
    Integer nodeId;
    String productName;
    private static ArrayList<String> processedLookups = new ArrayList<>();
    private static final int secondsToSleepFor = 5*1000;

    public Lookup(Integer ID, String productName) {
        this.nodeId = ID;
        this.productName = productName;
    }

    public void lookup(String itemName, int maxHopCount) throws Exception {
        /*
        Generate lookupId
         */
        String lookupId = UUID.randomUUID().toString();
        Stack<Integer> path = new Stack<>();
        /*
        Flood the lookup message
         */
        floodLookUps(itemName, maxHopCount, lookupId, path);

        /*
        Sleep for the timeout period.
        If no reply has been received for this transaction by the timeout period, mark this transaction as "to be ignored".
         */
        for(int i = 0; i < 5; i++) {
            Thread.sleep(secondsToSleepFor/5);
            if(Client.buyer.repliesToIgnore.contains(lookupId))
                break;
        }

        if(Client.buyer.repliesToIgnore.contains(lookupId) == false) {
        	Client.buyer.IgnoreReply(lookupId, true);
        }
    }

    public void floodLookUps(String itemName, int maxHopCount, String lookupId, Stack<Integer> path) throws Exception {
        /*
        Check if the current transaction has already been processed in this node
         */
        if(processedLookups.contains(lookupId))
            return ;

        processedLookups.add(lookupId);

        /*
        Check if the item being sold matches to the one requested.
        If yes, initiate a "reply".
         */
        if (itemName.equals(this.productName)) {
            Server.logger.info(String.format("Buyer [%d] is looking to buy [%s]. Will send a reply to that buyer.", path.firstElement(), productName));
            Seller.sendReply(path, lookupId, nodeId);
        }
        else if(maxHopCount > 0){
            /*
            Fetch the neighbors and flood lookups to all the neighbors.
             */
            path.push(nodeId);
            for (Integer ID : Nodes.neighbors) {
                URL url = new URL(Nodes.nodes.get(ID));
                System.out.println("Forwarding request to " + url.toString());
                try {
                    Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
                    SellerNode seller = (SellerNode) registry.lookup("SellerNode");
                    seller.floodLookUps(itemName, maxHopCount-1, lookupId, path);
                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                }

            }
        }

        return ;
    }
}