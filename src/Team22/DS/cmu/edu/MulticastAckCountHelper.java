package Team22.DS.cmu.edu;

public class MulticastAckCountHelper {
	private TimeStampedMessage message;
	private int count;
	
	public MulticastAckCountHelper(TimeStampedMessage m, int i){
		setMessage(m);
		setCount(i);
	}
	
	public MulticastAckCountHelper(Message m, int i){
		message = new TimeStampedMessage(m);
		setCount(i);
	}

	public TimeStampedMessage getMessage() {
		return message;
	}

	public void setMessage(TimeStampedMessage message) {
		this.message = message;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
