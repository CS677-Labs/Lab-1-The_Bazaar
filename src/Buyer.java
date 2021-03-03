import org.apache.xmlrpc.XmlRpcException;

import java.util.ArrayList;
import java.util.Random;

// TODO Test

public class Buyer {
    public int nodeId;
    private String product_name;
    // TODO Fetch from a config file
    private static final String[] products = new String[]{"Fish", "Boar", "Salt"};

    public Buyer(int nodeId)
    {
        this.nodeId = nodeId;
    }

    private Reply pickSeller(ArrayList<Reply> replies)
    {
        Random random = new Random();
        return replies.get(random.nextInt(replies.size()));
    }

    public void buyProduct(ArrayList<Reply> replies, String product_name) throws Exception
    {
        this.product_name = product_name;
        while(true) {
            Reply sellerPicked = pickSeller(replies);
            System.out.println("Attempting to buy from Seller with ID " + sellerPicked.sellerId);
            this.buy(sellerPicked.sellerId);
            Thread.sleep(5000);
        }
    }
    private void buy(int sellerId) throws Exception {
        RPCClient sellerNode = new RPCClient(sellerId);
        System.out.println("The " + product_name + " trade was " + (sellerNode.buy(product_name) ? "Successful" : "Failure"));
    }
    // TODO Each buyer randomly picks an item and attempts to purchase it; it then waits a random amount of time, then picks another item to buy and so on.
    private String pickProduct()
    {
        Random random = new Random();
        return products[random.nextInt(products.length)];
    }

}
