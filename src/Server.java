import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Server implements SellerNode {
    public static  String productName;
    public static Integer ID;
    public static Logger logger;

    public Server() {
    }

    public static void main(String[] args) throws Exception{
        String pathToConfigFile;
        String[] productsToSell;
        try {
            ID = Integer.parseInt(args[0]);
            pathToConfigFile = args[1];
            productsToSell = args[2].split(",");
            Seller.maxProductCount = Integer.parseInt(args[3]);
        }catch (Exception e){
            System.err.println("Incorrect arguments. Usage java -c destination Server {id} {pathToConfig} {products to sell separated by ,} {maxCount}");
            throw e;
        }

        logger = Logger.getLogger("ServerLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("server.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
        } catch (SecurityException | IOException exception) {
            exception.printStackTrace();
        }
        Seller.setProducts(productsToSell);
        Properties prop;
        productName = Seller.productName;
        // Read urls of all the nodes in my peer to peer network.
        try (InputStream input = new FileInputStream(pathToConfigFile)) {
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
        int port = new URL(Nodes.nodes.get(ID)).getPort();
        try {
            Server obj = new Server();
            SellerNode stub = (SellerNode) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("SellerNode", stub);

            System.err.printf("Hi. I am Node %d, running on port %d. I got %d number of product %s to sell." +
                    " Hit me up!\n", ID, port, Seller.productCount, Seller.productName);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public ArrayList<Reply> lookUp(String itemName, int maxHopCount) {
        System.out.printf("Looking up product %s\n", itemName);
        ArrayList<Reply> replies = new ArrayList<>();;
        Lookup lookup = new Lookup(ID, productName);
        try {
            replies = lookup.lookup(itemName, maxHopCount);
        }catch (Exception e){
            e.printStackTrace();
        }
        return replies;
    }

    public boolean sellProduct(String itemName) {
        System.out.printf("Selling product %s\n", itemName);
        return Seller.sellProduct(itemName);
    }
}