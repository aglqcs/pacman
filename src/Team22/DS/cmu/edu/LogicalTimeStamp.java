package Team22.DS.cmu.edu;

public class LogicalTimeStamp extends TimeStamp {
	private static final long serialVersionUID = 5L;
	private int timeStamp;

	public LogicalTimeStamp() {
		timeStamp = 0;
	}

	public LogicalTimeStamp(TimeStamp timeStamp) {
		LogicalTimeStamp t = (LogicalTimeStamp) timeStamp;
		this.timeStamp = t.getTimeStamp();
	}

	public void increment(TimeStampedMessage m) {
		LogicalTimeStamp lts = (LogicalTimeStamp) m.getTimeStamp();
		if (timeStamp <= lts.getTimeStamp())
			this.timeStamp = lts.getTimeStamp() + 1;
		else timeStamp++;
	}

	public void increment() {
		this.timeStamp++;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	@Override
	public void setLength(int n) {
	}

	@Override
	public void setIndex(int i) {
	}

	public String toString() {
		return "LogicalTimeStamp: " + timeStamp;
	}
	
	@Override
	public int compareTo(TimeStamp t){
		LogicalTimeStamp lt = (LogicalTimeStamp)t;
		if(this.timeStamp < lt.getTimeStamp()) return -1;
		if(this.timeStamp == lt.getTimeStamp()) return 0;
		return 1;
	}
}
