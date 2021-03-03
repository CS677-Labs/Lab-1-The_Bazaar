import org.apache.xmlrpc.XmlRpcException;

import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;

public class RunNode {

    public static void main (String[] argv) throws Exception {
        Properties prop;
        String mode = argv[0];
        int id = Integer.parseInt(argv[1]);
        String product_name = argv[2];
        String pathToConfigFile = argv[3];
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
        if (mode.equals("--buyer")){
            int max_hop = 1;
            if (argv.length > 4){
                 max_hop = Integer.parseInt(argv[4]);
            }

            try {
                Lookup l = new Lookup(id);
                Reply ids = l.lookup(product_name, max_hop);
                Buyer buyer = new Buyer(id);
                ArrayList<Reply> sellers = new ArrayList<Reply>();
                sellers.add(ids);
                buyer.buyProduct(sellers, product_name);
            }
            catch (Exception E) {
                System.out.println(E.toString());
            }
        }
        else if (mode.equals("--seller")) {
            int port = new URL(Nodes.nodes.get(id)).getPort();
            System.out.print(port);
            RPCServer server = new RPCServer(port, product_name, id);
            server.start();
            System.out.printf("RPC Server started at port %d%n", port);
        }
        else {
            System.err.println("Invalid option for argument mode. Options: --client, --server");
        }
    }
}

