import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Stack;

public interface SellerNode extends Remote {
	void floodLookUps(String itemName, int maxHopCount, String lookupId, Stack<Integer> path) throws RemoteException;
	boolean sellProduct(String productName) throws RemoteException;
	void sendReplyBackToBuyer(Stack<Integer> pathToTraverse, ReplyMessage messageToSend) throws RemoteException;
}
