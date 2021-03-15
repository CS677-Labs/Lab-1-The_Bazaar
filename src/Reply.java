import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Stack;

public class Reply {
    private Stack<Integer> pathToTraverse;
    private ReplyMessage messageToSend;

    public Reply(Stack<Integer> pathToTraverse, ReplyMessage messageToSend) {
        this.pathToTraverse = pathToTraverse;
        this.messageToSend = messageToSend;
    }

    public void reply(Integer buyerId) {
        sendReplyBackToBuyer();
    }

    public void sendReplyBackToBuyer() {
        System.out.println(String.format("PathToTraverse = %s", pathToTraverse.toString()));
        if (pathToTraverse.empty()) {
            /*
            We have reached the destination.
            Now process this reply that has been received.
             */
            Client.buyer.processReply(messageToSend);
        }
        else {
            System.out.println(String.format("Sending reply from [%d] to [%d] via [%d]",
                    messageToSend.sellerId, pathToTraverse.firstElement(), pathToTraverse.peek()));
            Integer nextDestination = pathToTraverse.pop();
            try {
                URL url = new URL(Nodes.nodes.get(nextDestination));
                Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
                SellerNode seller = (SellerNode) registry.lookup("SellerNode");
                seller.sendReplyBackToBuyer(pathToTraverse, messageToSend);
            }
            catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
            }
        }
    }
}
