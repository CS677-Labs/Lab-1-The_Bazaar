import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;


public class Client {
    public static Logger logger;

    private Client() {}

    public static void main (String[] args) throws Exception {
        int id = Integer.parseInt(args[0]);
        String configFilePath = args[1];
        String[] products = args[2].split(",");
        logger = Logger.getLogger("ClientLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("client.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
        } catch (SecurityException | IOException exception) {
            exception.printStackTrace();
            System.out.println("Failed to setup logging.");
        }
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
            List<Integer> idList = replies.stream().map(p -> p.sellerId).collect(Collectors.toList());
            logger.info(String.format("Obtained replies from nodes %s", idList.toString()));
            Buyer buyer = new Buyer(id, productName);
            Reply sellerPicked = buyer.pickSeller(replies);
            logger.info(String.format("Randomly picking %d as the seller and contacting it directly.", sellerPicked.sellerId));
            boolean success = buyer.buy(sellerPicked.sellerId);
            if(success){
                logger.info(String.format("Bought product %s from Peer ID %d", productName, sellerPicked.sellerId));
            }
            else{
                System.out.printf("Failed to buy product %s from Peer ID %d", productName, sellerPicked.sellerId);
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
