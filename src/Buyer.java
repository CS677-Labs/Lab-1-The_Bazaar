import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;

// TODO Test

public class Buyer {
    public int nodeId;
    public String productName;
    private static String[] products;

    public Buyer(int nodeId, String productName)
    {
        this.nodeId = nodeId;
        this.productName = productName;
    }

    public Reply pickSeller(ArrayList<Reply> replies)
    {
        Random random = new Random();
        return replies.get(random.nextInt(replies.size()));
    }

//    public boolean buyProduct(ArrayList<Reply> replies) throws Exception
//    {
//        Reply sellerPicked = pickSeller(replies);
//        this.buy(sellerPicked.sellerId);
//    }
    public boolean buy(int sellerId) throws Exception {
        URL url = new URL(Nodes.nodes.get(sellerId));
        try {
            Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
            SellerNode sellerNode = (SellerNode) registry.lookup("SellerNode");
            return sellerNode.sellProduct(this.productName);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return false;
    }
    // TODO Each buyer randomly picks an item and attempts to purchase it; it then waits a random amount of time, then picks another item to buy and so on.
    public static String pickProduct()
    {
        Random random = new Random();
        return products[random.nextInt(products.length)];
    }
    public static void setProducts(String[] buyerProducts)
    {
       products = buyerProducts;
    }

}
