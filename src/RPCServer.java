import java.net.URL;
import java.util.*;

import org.apache.xmlrpc.XmlRpcException;

import org.apache.xmlrpc.server.*;
import org.apache.xmlrpc.webserver.WebServer;

public class RPCServer {
    private static int port;
    public RPCServer(int port) throws XmlRpcException{
        WebServer webServer = new WebServer(port);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
        PropertyHandlerMapping phm = new PropertyHandlerMapping();

        // Todo: @Adarsh implements Lookup class
        phm.addHandler("Lookup", Lookup.class);
        phm.addHandler("Buy", Buy.class);
        // Todo: @brinda implements Buy class

        xmlRpcServer.setHandlerMapping(phm);
        try{
            webServer.start();
        } catch (Exception exception){
            System.err.println(String.format("Failed to start the server %s", exception.getMessage()));
        }

    }
}