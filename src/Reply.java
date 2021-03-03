import java.io.Serializable;

public class Reply implements Serializable {
	public int sellerId;
	public Reply(int ID)
	{
		this.sellerId = ID;
	}
	public int getValue() {
		return this.sellerId;
	}
}
