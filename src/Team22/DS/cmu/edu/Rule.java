/* File: Rule.java
 * 
 * Author: Ryan Cutler rcutler@andrew.cmu.edu
 * Author: Jeff Brandon jdbrando@andrew.cmu.edu
 * 
 * Date: 1-29-2015
 * 
 * Description: A Class for representing various rules for 
 * 	message handling.
 * */
package Team22.DS.cmu.edu;

public class Rule {
	public enum Action {DROP, DELAY, DUPLICATE};
	private Action action;
	private String src;
	private String dst;
	private String kind;
	private int seq_num;
	private Boolean duplicate;

	public Rule(String actionString) throws Exception {
		if(actionString.compareTo("delay")==0)
			this.action=Action.DELAY;
		if(actionString.compareTo("drop")==0)
			this.action=Action.DROP;
		if(actionString.compareTo("duplicate")==0)
			this.action=Action.DUPLICATE;
		if(action==null) throw new Exception("action not found");
		src = dst = kind = null;
		seq_num = -1;
		duplicate = null;
	}

	public Rule.Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public int getSeq_num() {
		return seq_num;
	}

	public void setSeq_num(int seq_num) {
		this.seq_num = seq_num;
	}

	public Boolean getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(Boolean duplicate) {
		this.duplicate = duplicate;
	}

	public boolean isMatch(Message m) {
		if (this.src != null && m.getSource()!=null && this.src.compareTo(m.getSource()) != 0)
			return false;
		if (this.dst != null && this.dst.compareTo(m.getDestination()) != 0)
			return false;
		if (this.kind != null && this.kind.compareTo(m.getKind()) != 0)
			return false;
		if (this.seq_num != -1 && this.seq_num != m.getSequenceNum())
			return false;
		if (this.duplicate != null && this.duplicate.compareTo(m.getDuplicate()) != 0)
			return false;
		return true;
	}
}
