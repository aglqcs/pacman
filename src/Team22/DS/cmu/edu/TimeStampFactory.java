package Team22.DS.cmu.edu;

public class TimeStampFactory {
	public static TimeStamp buildTimeStamp(TimeStampType type) {
		TimeStamp ts;
		switch (type) {
		case LOGICAL:
			ts = new LogicalTimeStamp();
			ts.setType(type);
			break;
		case VECTOR:
			ts = new VectorTimeStamp();
			ts.setType(type);
			break;
		default:
			ts = null;
		}
		return ts;
	}

	public static TimeStamp copyTimeStamp(TimeStamp timeStamp) {
		TimeStamp ts;
		switch(timeStamp.getType()){
		case LOGICAL:
			ts = new LogicalTimeStamp(timeStamp);
			ts.setType(TimeStampType.LOGICAL);
			break;
		case VECTOR:
			ts = new VectorTimeStamp(timeStamp);
			ts.setType(TimeStampType.VECTOR);
			break;
		default:
			ts = null;
		}
		return ts;
		
	}
}
