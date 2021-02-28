import java.util.UUID;
import java.util.concurrent.Semaphore;
// TODO: Test

// Todo: Brinda

// TODO: Rename to Seller?
public class Buy {
    public String productName;
    // TODO: Change integer nodeId to String in other files if used.
    public String nodeId;
    public int productCount;
    private final Semaphore semaphore;

    // fixme: Why do we need the product name param? Every seller sells only one product.
    private Buy(String productName) {
        this.semaphore = new Semaphore(1);
        this.productName = productName;
        this.nodeId = UUID.randomUUID().toString();
    }

    private boolean buyProduct() {
        boolean bought = false;
        try {
            if (this.semaphore != null)
                this.semaphore.acquire();
            if (this.productCount >= 1) {
                this.productCount -= 1;
                bought = true;
            }
        } catch (InterruptedException exc) {
            System.err.println(exc);
            // TODO: Log
        }

        this.semaphore.release();
        return bought;
    }

}
