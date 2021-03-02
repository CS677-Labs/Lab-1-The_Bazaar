import org.apache.xmlrpc.XmlRpcException;

import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;

public class RunNode {

    public static void main (String[] argv) throws IOException, XmlRpcException {
        Properties prop;
        String mode = argv[0];
        int id = Integer.parseInt(argv[1]);
        String product_name = argv[2];
        try (InputStream input = new FileInputStream("src/config.properties")) {
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
        if (mode.equals("--buyer")){
            int max_hop = 1;
            if (argv.length > 3){
                 max_hop = Integer.parseInt(argv[3]);
            }
            // Todo: Recurrent buys for random products
            Lookup l = new Lookup(id);
            ArrayList<Reply> ids = l.lookup(product_name, max_hop);
            Buyer buyer = new Buyer(id);
            buyer.buyProduct(ids);
        }
        else if (mode.equals("--seller")) {
            int port = new URL(Nodes.nodes.get(id)).getPort();
            RPCServer server = new RPCServer(port, product_name);
            server.start();
            System.out.printf("RPC Server started at port %d%n", port);
        }
        else {
            System.err.println("Invalid option for argument mode. Options: --client, --server");
        }
    }
}

