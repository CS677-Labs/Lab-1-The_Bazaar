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
    Thread t;
    public ServerThread(int ID){
            this.url = Nodes.nodes.get(ID);
            this.ID = ID;
    }

    @Override
    public void run() {
        System.out.printf("Node %d running as a Lookup server on url %s..\n", this.ID, url);
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

    public void floodLookUps(String itemName, int maxHopCount, String lookupId, Stack<Integer> path) {
        System.out.printf("Looking up product %s\n", itemName);
        Lookup lookup = new Lookup(Server.ID, Seller.productName);
        try {
            lookup.floodLookUps(itemName, maxHopCount, lookupId, path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ;
    }

    public boolean sellProduct(String itemName) {
        Server.logger.info(String.format("Selling product %s\n", itemName));
        return Seller.sellProduct(itemName);
    }

	@Override
	public void sendReplyBackToBuyer(Stack<Integer> pathToTraverse, ReplyMessage messageToSend) {
        Reply reply = new Reply(pathToTraverse, messageToSend);
        reply.sendReplyBackToBuyer();
	}
}

public class Server {
    public static Logger logger;
    public static Integer ID;
    public static void main(String[] args) throws Exception{
        String pathToConfigFile;
        String[] productsToSell;
        String[] productsToRestock;
        try {
            ID = Integer.parseInt(args[0]);
            pathToConfigFile = args[1];
            productsToSell = args[2].split(",");
            Seller.maxProductCount = Integer.parseInt(args[3]);
            if(args.length>=5)
                productsToRestock = args[4].split(",");
            else
                productsToRestock = productsToSell;

        }catch (Exception e){
            System.err.println("Incorrect arguments. Usage java -c destination Server {id} {pathToConfig} {products to sell separated by ,} {maxCount} {[optional] products to restock separated by ,}");
            throw e;
        }

        logger = Logger.getLogger("ServerLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(String.format("Node_%d_server.log", ID), true);
            logger.addHandler(fh);
            MyLogFormatter formatter = new MyLogFormatter(ID);
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
        } catch (SecurityException | IOException exception) {
            exception.printStackTrace();
        }
        Seller.setProducts(productsToSell);
        Seller.setProductsToRestock(productsToRestock);
        Properties prop;
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
        ServerThread serverThread = new ServerThread(ID);
        serverThread.start();
        System.err.printf("Hi. I am node %d running as a seller. I got %d number of product %s to sell." +
                " Hit me up!\n", ID, Seller.maxProductCount, Seller.productName);

    }
}