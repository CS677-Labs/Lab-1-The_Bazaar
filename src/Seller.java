import java.util.concurrent.Semaphore;
// TODO: Test
// TODO: refill when one item is over
/**
 * Each seller picks one of three items to sell.
 * Each buyer randomly picks an item and attempts to purchase it; it then waits a random amount of time, then picks another item to buy and so on.
 * Each seller starts with m items (e.g., m boars) to sell; upon selling all m items, the seller picks another item at random and becomes a seller of that item.
 */
public class Seller {
    private final Semaphore semaphore;
    public String productName;
    public int nodeId;
    public int productCount;

    public Seller(String productName, int nodeId) {
        this.semaphore = new Semaphore(1);
        this.productName = productName;
        this.nodeId = nodeId;
    }

    private boolean sellProduct() {
        boolean bought = false;
        try {
            this.semaphore.acquire();
            if (this.productCount >= 1) {
                this.productCount -= 1;
                bought = true;
            }
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        this.semaphore.release();
        return bought;
    }

}
