import java.io.Serializable;

public class ReplyMessage implements Serializable {
	public int sellerId;
	public String transactionId;

	public ReplyMessage(String transactionId, int sellerId) {
		this.transactionId = transactionId;
		this.sellerId = sellerId;
	}
}
