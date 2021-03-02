import org.apache.xmlrpc.XmlRpcException;

import java.util.ArrayList;
import java.util.Random;

// TODO Test

public class Buyer {
    public int nodeId;
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

    public void buyProduct(ArrayList<Reply> replies) throws XmlRpcException
    {
        Reply sellerPicked = pickSeller(replies);
        this.buy(sellerPicked.sellerId);
    }
    private void buy(int sellerId) throws XmlRpcException {
        RPCClient sellerNode = new RPCClient(sellerId);
        sellerNode.buy();
    }
    // TODO Each buyer randomly picks an item and attempts to purchase it; it then waits a random amount of time, then picks another item to buy and so on.
    private String pickProduct()
    {
        Random random = new Random();
        return products[random.nextInt(products.length)];
    }

}
