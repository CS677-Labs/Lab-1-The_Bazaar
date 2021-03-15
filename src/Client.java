import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Client {
    public static Logger logger;
    public static Buyer buyer;

    private Client() {}

    public static void main (String[] args) throws Exception {
        int id = Integer.parseInt(args[0]);
        String configFilePath = args[1];
        String[] products = args[2].split(",");
        logger = Logger.getLogger("ClientLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(String.format("Node_%d_client.log", id), true);
            MyLogFormatter formatter = new MyLogFormatter(id);
            fh.setFormatter(formatter);
            logger.addHandler(fh);
            // the following statement is used to log any messages
        } catch (SecurityException | IOException exception) {
            exception.printStackTrace();
            System.out.println("Failed to setup logging.");
        }
        buyer = new Buyer(id);
        buyer.setProducts(products);
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
        while(true){
            try {
                System.out.println("Sleeping for two seconds......");
                Thread.sleep(2000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }

            String productName = buyer.pickProduct();
            System.out.println(String.format("Sending lookup request for product %s", productName));
            
            // Passing productName as empty string because a buyer doesn't sell any product.
            // Lookup is used by the server nodes too where they pass the product name they sell to this.
            Lookup lookup = new Lookup(id, "");

            try{
                lookup.lookup(productName, 10);
            } catch (Exception e){
                e.printStackTrace();
                throw e;
            }
        }
    }
}
