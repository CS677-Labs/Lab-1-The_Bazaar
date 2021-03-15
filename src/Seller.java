import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Semaphore;

public class Seller {
    private static String[] productsToRestock;
    private static final Semaphore semaphore = new Semaphore(1);
    public static int maxProductCount;
    public static int productCount;
    public static String productName;
    private static String[] products;

    /*
    Each seller picks one of three items to sell.
    Each seller starts with m items (e.g., m boars) to sell; upon selling all m items, the seller picks another item at random and becomes a seller of that item.
     */
    private static void restock() {
        Random random = new Random();
        productName = productsToRestock[random.nextInt(productsToRestock.length)];
        productCount = maxProductCount;
        Server.logger.info(String.format("Restocking the product with %s", productName));
    }

    public static boolean sellProduct(String itemName) {
        if (!itemName.equals(productName))
            return false;

        boolean bought = false;

        try {
            semaphore.acquire();
            Server.logger.info("Acquired lock");
            Server.logger.info(String.format("Current count of product %s is %d", itemName, productCount));
            if (productCount >= 1) {
                productCount -= 1;
                bought = true;
                if (productCount == 0)
                    restock();
            }
            Server.logger.info(String.format("Count after buy is %d", productCount));


        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        semaphore.release();
        Server.logger.info("Released lock");
        return bought;
    }

    public static void setProducts(String[] productsToSell) {
        productName= productsToSell[0];
        productCount = maxProductCount;
    }

    public static void sendReply(Stack<Integer> path, String transactionId, Integer nodeId) {
        ReplyMessage replyMessage = new ReplyMessage(transactionId, nodeId);
        Integer buyerId = path.firstElement();
        Reply reply = new Reply(path, replyMessage);
        reply.reply(buyerId);
    }

	public static void setProductsToRestock(String[] productList) {
        productsToRestock = productList;
    }
}
