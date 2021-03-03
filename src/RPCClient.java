import java.net.URL;
import java.util.*;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.*;

public class RPCClient {
    XmlRpcClient client;
    public RPCClient(int id) throws Exception {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = Nodes.nodes.get(id);
            config.setServerURL(new URL(url));
            config.setEnabledForExtensions(true);
            config.setConnectionTimeout(60 * 1000);
            config.setReplyTimeout(60 * 1000);
            this.client = new XmlRpcClient();
            this.client.setTransportFactory(
                    new XmlRpcCommonsTransportFactory(client));
            this.client.setConfig(config);

        } catch (Exception exception) {
            System.err.println("JavaClient: " + exception);
            throw exception;
        }
    }
    public ArrayList<Reply> lookUp(String product_name, int hop_max) throws XmlRpcException {
        Object[] params = new Object[]{product_name, hop_max};
        ArrayList<Reply> result = (ArrayList<Reply>) this.client.execute("Lookup.lookup", params);
        return result;
    }
    public boolean buy() throws XmlRpcException{
        Object[] params = new Object[]{};
        return (boolean) this.client.execute("Seller.sellProduct", params);
    }

}
