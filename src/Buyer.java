import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Buyer {
    private static String[] products;
    public int nodeId;
    public String productName;
    public ArrayList<String> repliesToIgnore;
    private static final Semaphore semaphore = new Semaphore(1);

    public Buyer(int nodeId) {
        this.nodeId = nodeId;
        repliesToIgnore = new ArrayList<>();
    }
    
    public Buyer(int nodeId, String productName) {
        this.nodeId = nodeId;
        this.productName = productName;
    }

    /*
    Randomly picks a product from the already set list of valid products.
     */
    public String pickProduct() {
        Random random = new Random();
        productName = products[random.nextInt(products.length)];
        return productName;
    }

    /*
    Funtion to set the list of products that this buyer should randomly pick from.
     */
    public void setProducts(String[] buyerProducts) {
        products = buyerProducts;
    }

    /*
    A node is provided the buy(sellerID) interface here.
    Using this a node can contact the seller directly and purchase the product.
     */
    public boolean buy(int sellerId) {
        try {
        	URL url = new URL(Nodes.nodes.get(sellerId));
            Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
            SellerNode sellerNode = (SellerNode) registry.lookup("SellerNode");
            return sellerNode.sellProduct(this.productName);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        return false;
    }

    /*
    This method is called when a Reply is received at the buyer.
    If this request has timed out or has been already processed, the it will be added to repliesToIgnore.

    Only if a reply is not to be ignored, it will be processed here.
     */
    public void processReply(ReplyMessage replyMessage) {
    	try {
			semaphore.acquire();
			if(repliesToIgnore.contains(replyMessage.transactionId))
				Client.logger.info("Ignorning reply from Seller Node " + replyMessage.sellerId);
			else {
				Client.logger.info(String.format("Received reply from seller node %d. Contacting it directly", replyMessage.sellerId));
				if(buy(replyMessage.sellerId)){
				    Client.logger.info(String.format("Bought product %s from Peer ID %d\n", productName, replyMessage.sellerId));
				    repliesToIgnore.add(replyMessage.transactionId);
				}
				else{
				    System.out.printf("Failed to buy product %s from Peer ID %d\n", productName, replyMessage.sellerId);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	semaphore.release();
    }

    /*
    Method to add a reply to the ignore list.
     */
	public void IgnoreReply(String lookupId, boolean timedOut) {
		try {
			semaphore.acquire();
			if(timedOut)
				Client.logger.info("Did not get a reply for the lookup request of " + productName + 
	        			". Timing out and ignoring future replies for this request");
			repliesToIgnore.add(lookupId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		semaphore.release();
	}
}
