package Team22.DS.cmu.edu;

import java.util.ArrayList;

public class Group {
	private String name;
	private int groupSeqNum;
	private ArrayList<String> members;

	public Group() {
		members = new ArrayList<String>();
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addMember(String member) {
		members.add(member);
	}

	public void removeMember(int index) {
		members.remove(index);
	}

	public int getGroupSeqNum() {
		return groupSeqNum;
	}

	public void setGroupSeqNum(int groupSeqNum) {
		this.groupSeqNum = groupSeqNum;
	}

	public boolean isMemberOf(String name) {
		for (String member : members) {
			if (member.compareTo(name) == 0)
				return true;
		}
		return false;
	}
}
