import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SellerNode extends Remote {
	ArrayList<Reply> floodLookUps(String itemName, int maxHopCount, String lookupId) throws RemoteException;
	boolean sellProduct(String productName) throws RemoteException;
}
