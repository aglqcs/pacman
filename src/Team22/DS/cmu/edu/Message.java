/* File: Message.java
 * 
 * Author: Ryan Cutler rcutler@andrew.cmu.edu
 * Author: Jeff Brandon jdbrando@andrew.cmu.edu
 * 
 * Date: 1-29-2015
 * 
 * Description: A Class for representing a message to be 
 * 	sent or received.
 * */
package Team22.DS.cmu.edu;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int SequenceNum;
	private int GroupSeqNum;
	private String Source;
	private Boolean Duplicate;
	private String Destination;
	private String Kind;
	private Object Data;
	private boolean noRecvDelay;
	private boolean isGroupMsg;
	private String groupName;

	public Message(String dest, String kind, Object data) {
		System.out.println("sss");

		this.setDestination(dest);
		this.setKind(kind);
		this.setData(data);
		this.setDuplicate(false);
		noRecvDelay = false;
		setGroupMsg(false);
		System.out.println("sss");
	}

	public Message(Message m) {
		this.setDestination(m.getDestination());
		this.setKind(m.getKind());
		this.setData(m.getData());
		this.setDuplicate(m.getDuplicate());
		this.setSequenceNum(m.getSequenceNum());
		this.setSource(m.getSource());
		this.setGroupMsg(m.isGroupMsg());
		this.setGroupSeqNum(m.getGroupSeqNum());
		this.setGroupName(m.getGroupName());
		this.setNoRecvDelay(m.isNoRecvDelay());
	}

	public String getDestination() {
		return Destination;
	}

	public void setDestination(String destination) {
		Destination = destination;
	}

	public String getKind() {
		return Kind;
	}

	public void setKind(String kind) {
		Kind = kind;
	}

	public Object getData() {
		return Data;
	}

	public void setData(Object data) {
		Data = data;
	}

	public Boolean getDuplicate() {
		return Duplicate;
	}

	public void setDuplicate(Boolean duplicate) {
		Duplicate = duplicate;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public int getSequenceNum() {
		return SequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		SequenceNum = sequenceNum;
	}

	public boolean isNoRecvDelay() {
		return noRecvDelay;
	}

	public void setNoRecvDelay(boolean noRecvDelay) {
		this.noRecvDelay = noRecvDelay;
	}

	public boolean isGroupMsg() {
		return isGroupMsg;
	}

	public void setGroupMsg(boolean isGroupMsg) {
		this.isGroupMsg = isGroupMsg;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupSeqNum() {
		return GroupSeqNum;
	}

	public void setGroupSeqNum(int groupSeqNum) {
		GroupSeqNum = groupSeqNum;
	}
}
