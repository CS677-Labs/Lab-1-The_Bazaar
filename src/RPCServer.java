import java.io.IOException;
import org.apache.xmlrpc.XmlRpcException;

import org.apache.xmlrpc.server.*;
import org.apache.xmlrpc.webserver.WebServer;

public class RPCServer {
    WebServer webServer;
    public static String productName;
    int port;
    public RPCServer(int port, String productName) throws XmlRpcException{
        this.webServer = new WebServer(port);
        this.port = port;
        RPCServer.productName = productName;
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.addHandler("Lookup", Lookup.class);
        phm.addHandler("Seller", Seller.class);
        xmlRpcServer.setHandlerMapping(phm);
    }
    public void start() throws IOException {
        try{
            webServer.start();
            System.out.printf("Hi there! I sell product %s. I am now up and running on port %s.",
                    this.productName, this.port);
        } catch (Exception exception){
            System.err.println(String.format("Failed to start the server %s", exception.getMessage()));
            throw exception;
        }
    }
}