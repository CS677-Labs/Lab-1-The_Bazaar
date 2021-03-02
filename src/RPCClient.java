import java.net.URL;
import java.util.*;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.*;

public class RPCClient {
    XmlRpcClient client;
    public RPCClient(int id) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = Nodes.nodes.get(id);
            config.setServerURL(new URL(url));
            this.client = new XmlRpcClient();
            this.client.setConfig(config);

        } catch (Exception exception) {
            System.err.println("JavaClient: " + exception);
        }
    }
    public ArrayList<Integer> lookUp(String product_name, int hop_max) throws XmlRpcException {
        Object[] params = new Object[]{product_name, hop_max};
        ArrayList<Integer> result = (ArrayList<Integer>) this.client.execute("Lookup.lookup", params);
        return result;
    }
    public boolean buy() throws XmlRpcException{
        Object[] params = new Object[]{};
        return (boolean) this.client.execute("Seller.sell", params);
    }

}
