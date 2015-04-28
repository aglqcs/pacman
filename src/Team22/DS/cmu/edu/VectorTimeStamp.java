package Team22.DS.cmu.edu;

public class VectorTimeStamp extends TimeStamp {
	private static final long serialVersionUID = 4L;
	private int thisNodesIndex;
	private int length;
	private int[] timeStamp;

	public VectorTimeStamp() {
	}

	public VectorTimeStamp(TimeStamp timeStamp) {
		VectorTimeStamp t = (VectorTimeStamp) timeStamp;
		setLength(t.getLength());
		setIndex(t.getIndex());
		for (int i = 0; i < length; i++)
			this.timeStamp[i] = t.getTimeStamp()[i];
	}

	public void setIndex(int i) {
		thisNodesIndex = i;
	}

	public int getIndex() {
		return thisNodesIndex;
	}

	public void setLength(int n) {
		timeStamp = new int[n];
		length = n;
	}

	public int getLength() {
		return length;
	}

	public void increment(TimeStampedMessage m) {
		VectorTimeStamp vts = (VectorTimeStamp) m.getTimeStamp();
		for (int i = 0; i < length; i++) {
			if (i == thisNodesIndex)
				timeStamp[i]++;
			else {
				if (timeStamp[i] < vts.getTimeStamp()[i])
					timeStamp[i] = vts.getTimeStamp()[i];
			}
		}
	}

	public void increment() {
		timeStamp[thisNodesIndex]++;
	}

	public int[] getTimeStamp() {
		return timeStamp;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("VectorTimeStamp: ["
				+ timeStamp[0]);
		for (int i = 1; i < length; i++) {
			sb.append(", " + timeStamp[i]);
		}
		sb.append("], ThisIndex: " + thisNodesIndex);
		return sb.toString();
	}

	@Override
	public int compareTo(TimeStamp t) {
		VectorTimeStamp vt = (VectorTimeStamp) t;
		boolean isPos=false;
		int i;
		int res[] = new int[length];
		for (i = 0; i < length; i++) {
			if (timeStamp[i] < vt.getTimeStamp()[i])
				res[i] = -1;
			else if (timeStamp[i] == vt.getTimeStamp()[i])
				res[i] = 0;
			else
				res[i] = 1;
		}
		for (i = 0; i < length; i++) {
			if (res[i] == 0)
				continue;
			if (res[i] == -1) {
				isPos = false;
				break;
			}
			if (res[i] == 1) {
				isPos = true;
				break;
			}
		}
		if (i == length)
			return 0;
		for (i++; i < length; i++) {
			if(res[i]==1 && !isPos) return 0; //concurrent
			if(res[i]==-1 && isPos) return 0;
		}
		if(isPos) return 1;
		else return -1;
	}
	
}
