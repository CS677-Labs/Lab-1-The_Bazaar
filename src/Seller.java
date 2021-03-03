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
    private static String[] products;
    private static final Semaphore semaphore = new Semaphore(1);;
    public static int maxProductCount;
    public static int productCount;
    public static String productName;


    private static void restock() {
        Random random = new Random();
        productName = products[random.nextInt(products.length)];
        productCount = maxProductCount;
        Server.logger.info(String.format("Restocking the product with %s", productName));
    }

    public static boolean sellProduct(String itemName) {
        if (!itemName.equals(productName))
            return false;

        boolean bought = false;

        try {
            semaphore.acquire();
            System.out.printf("Acquired lock\n");
            System.out.printf("Current count of product %s is %d\n", itemName, productCount);
            if (productCount >= 1) {
                productCount -= 1;
                bought = true;
                if (productCount == 0)
                    restock();
            }
            System.out.printf("Count after buy is %d\n", productCount);


        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        semaphore.release();
        return bought;
    }
    public static void setProducts(String[] productsToSell)
    {
        products = productsToSell;
        productName= products[0];
        productCount = maxProductCount;
    }

}
