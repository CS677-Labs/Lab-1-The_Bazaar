import java.util.concurrent.Semaphore;
// TODO: Test

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
