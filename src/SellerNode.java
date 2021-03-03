import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SellerNode extends Remote {
	ArrayList<Reply> lookUp(String itemName, int maxHopCount) throws RemoteException;
	boolean sellProduct(String productName) throws RemoteException;
}
