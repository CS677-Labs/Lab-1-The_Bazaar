import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;

public class Buyer {
    private static String[] products;
    public int nodeId;
    public String productName;

    public Buyer(int nodeId, String productName) {
        this.nodeId = nodeId;
        this.productName = productName;
    }

    public static String pickProduct() {
        Random random = new Random();
        return products[random.nextInt(products.length)];
    }

    public static void setProducts(String[] buyerProducts) {
        products = buyerProducts;
    }

    public Reply pickSeller(ArrayList<Reply> replies) {
        Random random = new Random();
        return replies.get(random.nextInt(replies.size()));
    }

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

}
