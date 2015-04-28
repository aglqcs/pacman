package Team22.DS.cmu.edu;

public class TimeStampedMessage extends Message {

	private static final long serialVersionUID = 3L;

	private TimeStamp timeStamp;

	public TimeStampedMessage(String dest, String kind, Object data) {
		super(dest, kind, data);
	}

	public TimeStampedMessage(Message m) {
		super(m);
	}
	
	public TimeStampedMessage(TimeStampedMessage m) {
		super(m);
		this.timeStamp = m.getTimeStamp();
	}

	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = TimeStampFactory.copyTimeStamp(timeStamp);
	}
}
