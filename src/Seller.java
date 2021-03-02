import java.util.ArrayList;
import java.util.Arrays;
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
    private final Semaphore semaphore;
    public String productName;
    public int nodeId;
    public int productCount;
    // TODO Fetch from a config file
    private static final String[] items = new String[]{"Fish", "Boar", "Salt"};

    //TODO Fetch from config file
    private int maxProductCount = 50;

    public Seller(String productName, int nodeId) {
        this.semaphore = new Semaphore(1);
        this.productName = productName;
        this.nodeId = nodeId;
    }

    private void restock(){
        Random random = new Random();
        this.productName = items[random.nextInt(items.length)];
        this.productCount = this.maxProductCount;
    }
    private boolean sellProduct() {
        boolean bought = false;
        try {
            this.semaphore.acquire();
            if (this.productCount >= 1) {
                this.productCount -= 1;
                bought = true;
            }
            else
                restock();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        this.semaphore.release();
        return bought;
    }

}
