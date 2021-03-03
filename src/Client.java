import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;


public class Client {

    private Client() {}

    public static void main (String[] args) throws Exception {
        int id = Integer.parseInt(args[0]);
        String configFilePath = args[1];
        String[] products = args[2].split(",");
        Buyer.setProducts(products);
        Properties prop;
        try (InputStream input = new FileInputStream(configFilePath)) {
            prop = new Properties();
            // load a properties file
            prop.load(input);
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                Nodes.nodes.put(Integer.parseInt((String)entry.getKey()), (String) entry.getValue());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
        for (int i =0; i< 10; i++){
            String productName = Buyer.pickProduct();

            System.out.printf("--------------------------------\nBuying product %s\n", productName);
            // Passing productName as empty string because a buyer doesn't sell any product. Lookup is used by the server nodes too wher they pass the product name they sell to this.
            Lookup lookup = new Lookup(id, "");
            ArrayList<Reply> replies;
            try{
                replies = lookup.lookup(productName, 2);
            } catch (Exception e){
                e.printStackTrace();
                throw e;
            }
            boolean success = false;
            if(false == replies.isEmpty()) {
                Buyer buyer = new Buyer(id, productName);
                Reply sellerPicked = buyer.pickSeller(replies);
                success = buyer.buy(sellerPicked.sellerId);
            }
            if(success){
                System.out.printf("Bought product %s\n", productName);
            }
            else{
                System.out.printf("Failed to buy product %s\n", productName);
            }
            try
            {
                Thread.sleep(2000);
                System.out.println("Sleeping for two seconds......\n--------------------------");

            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }

    }

}
