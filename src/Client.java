import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
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
            fh = new FileHandler("client.log", true);
            MyLogFormatter formatter = new MyLogFormatter(id);
            fh.setFormatter(formatter);
            logger.addHandler(fh);
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
                Integer key = Integer.parseInt((String) entry.getKey());
                String value = (String) entry.getValue();
                String[] URLandNeighbors = value.split(",");
                Nodes.nodes.put(key, URLandNeighbors[0]);

                if(key == id) {
                    for (int i = 1; i < URLandNeighbors.length; i++)
                        Nodes.neighbors.add(Integer.parseInt(URLandNeighbors[i]));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
        ServerThread serverThread = new ServerThread(id);
        serverThread.start();
        for (int i =0; i< 10; i++){
            try
            {
                Thread.sleep(2000);
                System.out.println("Sleeping for two seconds......\n--------------------------");

            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            String productName = Buyer.pickProduct();

            System.out.printf("--------------------------------\nBuying product %s\n", productName);
            // Passing productName as empty string because a buyer doesn't sell any product.
            // Lookup is used by the server nodes too where they pass the product name they sell to this.
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

            Reply sellerPicked;
            if(!replies.isEmpty()) {
                Buyer buyer = new Buyer(id, productName);
                sellerPicked = buyer.pickSeller(replies);
                logger.info(String.format("Randomly picking %d as the seller and contacting it directly.", sellerPicked.sellerId));
                if(buyer.buy(sellerPicked.sellerId)){
                    logger.info(String.format("Bought product %s from Peer ID %d\n", productName, sellerPicked.sellerId));
                }
                else{
                    System.out.printf("Failed to buy product %s from Peer ID %d\n", productName, sellerPicked.sellerId);

                }
            }
            else{
                logger.info(String.format("Could not buy product %s because found no peers with that item\n", productName));
            }

        }

    }

}
