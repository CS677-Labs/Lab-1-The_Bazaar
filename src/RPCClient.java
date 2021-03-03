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
            this.client.setConfig(config);

        } catch (Exception exception) {
            System.err.println("JavaClient: " + exception);
            throw exception;
        }
    }
    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }
    public Reply lookUp(String product_name, int hop_max) throws XmlRpcException {
        Object[] params = new Object[]{product_name, hop_max};
        Object obj = this.client.execute("Lookup.lookup", params);
        Reply result = (Reply)(obj);
        return result;
    }
    public boolean buy(String product_name) throws XmlRpcException{
        Object[] params = new Object[]{product_name};
        return (boolean) this.client.execute("Seller.sellProduct", params);
    }

}
