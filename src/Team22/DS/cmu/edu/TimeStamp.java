package Team22.DS.cmu.edu;

import java.io.Serializable;

public abstract class TimeStamp implements Serializable {

	private static final long serialVersionUID = 2L;
	private TimeStampType type;

	public TimeStampType getType() {
		return type;
	}

	protected void setType(TimeStampType type) {
		this.type = type;
	}

	public abstract void increment();

	public abstract void increment(TimeStampedMessage m);

	public abstract void setLength(int n);

	public abstract void setIndex(int i);

	public abstract String toString();
	
	public abstract int compareTo(TimeStamp t);
}
