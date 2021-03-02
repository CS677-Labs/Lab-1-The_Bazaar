import org.apache.xmlrpc.XmlRpcException;

import java.util.ArrayList;
import java.util.Random;

// TODO: Test

public class Buyer {
    public int nodeId;
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
}
