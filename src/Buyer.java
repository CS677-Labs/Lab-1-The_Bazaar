import java.util.ArrayList;
import java.util.Random;

// TODO: Test
// TODO: Pick random item to buy

public class Buyer {
    public int nodeId;
    public Buyer(int nodeId)
    {
        this.nodeId = nodeId;
    }

    public Reply pickSeller(ArrayList<Reply> replies)
    {
        Random random = new Random();
        return replies.get(random.nextInt(replies.size()));
    }

    public void buyProduct(ArrayList<Reply> replies)
    {
        Reply sellerPicked = pickSeller(replies);
        // TODO: communicate with the sellerPicked directly to buy the product
    }

}
