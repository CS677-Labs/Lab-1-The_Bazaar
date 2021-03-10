import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


class ServerThread implements Runnable {
    String url;
    int ID;
    int productCount;
    Thread t;
    String productName;
    public ServerThread(int ID, int productCount, String productName){
            this.url = Nodes.nodes.get(ID);
            this.ID = ID;
            this.productCount = productCount;
            this.productName = productName;
    }

    @Override
    public void run() {
        System.out.printf("Server running on url %s..", url);
        int port;
        try {
            port = new URL(this.url).getPort();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to start the Server");
        }
        try {
            ServerRMI obj = new ServerRMI();
            SellerNode stub = (SellerNode) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("SellerNode", stub);

            System.err.printf("Hi. I am Node %d, running on url %s. I got %d number of product %s to sell." +
                    " Hit me up!\n", this.ID, this.url, this.productCount, this.productName);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
            throw new RuntimeException("Failed to start the server");
        }
    }
    public void start () {
        if (t == null) {
            t = new Thread (this, "Server");
            t.start ();
        }
    }
}

class ServerRMI implements SellerNode {


    public ServerRMI() {

    }

    public ArrayList<Reply> floodLookUps(String itemName, int maxHopCount, String lookupId) {
        System.out.printf("Looking up product %s\n", itemName);
        ArrayList<Reply> replies = new ArrayList<>();;
        Lookup lookup = new Lookup(Server.ID, Server.productName);
        try {
            replies = lookup.floodLookUps(itemName, maxHopCount, lookupId);
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

public class Server {
    public static Logger logger;
    public static String productName;
    public static Integer ID;
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

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

        logger = Logger.getLogger("ServerLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("server.log", true);
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
        /*
         * Config file to have below structure
         * NodeID=<URLofNode>,<Comma separated list of neighbors>
         * 1=http://127.0.0.1:5000,2,3
         * 2=http://127.0.0.1:5001,3,1
         * 3=http://127.0.0.1:5002,1,3
         */
        try (InputStream input = new FileInputStream(pathToConfigFile)) {
            prop = new Properties();
            // load a properties file
            prop.load(input);
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                int key = Integer.parseInt((String) entry.getKey());
                String value = (String) entry.getValue();
                String[] URLandNeighbors = value.split(",");
                Nodes.nodes.put(key, URLandNeighbors[0]);

                if (key == ID) {
                    for (int i = 1; i < URLandNeighbors.length; i++)
                        Nodes.neighbors.add(Integer.parseInt(URLandNeighbors[i]));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
        ServerThread serverThread = new ServerThread(ID, Seller.maxProductCount, productName);
        serverThread.start();
    }
}