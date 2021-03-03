import java.util.Random;
import java.util.concurrent.Semaphore;
// TODO: Test
// TODO: refill when one item is over

/**
 * Each seller picks one of three items to sell.
 * Each seller starts with m items (e.g., m boars) to sell; upon selling all m items, the seller picks another item at random and becomes a seller of that item.
 */
// fixme What's restock forever? If you restock forever, the seller will never change the product right?

public class Seller {
    // TODO Fetch from a config file
    private static final String[] items = new String[]{"Fish"/*, "Boar", "Salt"*/};
    private final Semaphore semaphore;
    public String productName;
    public int nodeId;
    public static int productCount;
    //TODO Fetch from config file
    private final int maxProductCount = 50;

    public Seller() {
        this.semaphore = new Semaphore(1);
        this.productName = RPCServer.productName;
        this.nodeId = RPCServer.ID;
        productCount = 10;
    }

    private void restock() {
        Random random = new Random();
        this.productName = items[random.nextInt(items.length)];
        this.productCount = this.maxProductCount;
    }

    /*
    sellProduct will be executed remotely using RPC.
     */
    public boolean sellProduct(String productName) {
        if (!productName.equals(this.productName))
            return false;

        if (this.productCount == 0)
            restock();

        boolean bought = false;
        try {
            this.semaphore.acquire();
            if (this.productCount >= 1) {
                this.productCount -= 1;
                bought = true;
                if (this.productCount == 0)
                    restock();
            }

        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        this.semaphore.release();

        System.out.println("Sold " + productName + ". " + productCount + " remaining.");
        return bought;
    }

}
