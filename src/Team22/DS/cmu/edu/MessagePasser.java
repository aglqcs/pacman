/* File: MessagePasser.java
 * 
 * Author: Ryan Cutler rcutler@andrew.cmu.edu
 * Author: Jeff Brandon jdbrando@andrew.cmu.edu
 * 
 * Date: 1-29-2015
 * 
 * Description: A class for interacting with other nodes in
 * 	the communication system.
 * */
package Team22.DS.cmu.edu;

import jp.or.iidukat.example.pacman.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.io.*;

import org.yaml.snakeyaml.Yaml;

import android.util.Log;

public class MessagePasser {
	private String Name;
	public ArrayList<TimeStampedMessage> ReceiveBuffer;
	public ArrayList<TimeStampedMessage> SendBuffer;
	public BlockingQueue<TimeStampedMessage> MessageReceivedQueue = new ArrayBlockingQueue<TimeStampedMessage>(
			50);

	public LinkedHashMap<Group, ArrayList<MulticastAckCountHelper>> MulitcastReceiveBuffer;
	private int SequenceNum;
	private String ConfigFile;
	private long ModifiedTime;
	private ArrayList<Group> groups;
	private ArrayList<Rule> sendRules;
	private ArrayList<Node> nodes;
	private ArrayList<Rule> receiveRules;
	private TimeStamp timeStamp;

	public MessagePasser(String configuration_filename, String local_name,
			TimeStampType type) throws Exception {
		Name = local_name;
		SequenceNum = 0;
		timeStamp = TimeStampFactory.buildTimeStamp(type);

		ConfigFile = configuration_filename;
		sendRules = new ArrayList<Rule>();
		receiveRules = new ArrayList<Rule>();
		nodes = new ArrayList<Node>();
		groups = new ArrayList<Group>();
		ReceiveBuffer = new ArrayList<TimeStampedMessage>();
		SendBuffer = new ArrayList<TimeStampedMessage>();

		File file = new File(ConfigFile);
		ModifiedTime = file.lastModified();
		parseConfigFile(file, true);
		this.MulitcastReceiveBuffer = new LinkedHashMap<Group, ArrayList<MulticastAckCountHelper>>();
		for (Group g : groups)
			this.MulitcastReceiveBuffer.put(g,
					new ArrayList<MulticastAckCountHelper>());

		if (type == TimeStampType.VECTOR) {
			timeStamp.setLength(nodes.size());
		}
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			if (n.getName().compareTo(this.Name) == 0) {
				if (type == TimeStampType.VECTOR)
					timeStamp.setIndex(i);
				ListenerThread listener = new ListenerThread(n.getPort(), this);
				Thread t = new Thread(listener);
				t.start();
				break;
			}
		}
	}

	public MessagePasser(ArrayList<Node> nodes, String local_name,
			TimeStampType type) {
		Name = local_name;
		SequenceNum = 0;
		timeStamp = TimeStampFactory.buildTimeStamp(type);

		ConfigFile = null;
		sendRules = new ArrayList<Rule>();
		receiveRules = new ArrayList<Rule>();
		this.nodes = nodes;
		groups = new ArrayList<Group>();
		ReceiveBuffer = new ArrayList<TimeStampedMessage>();
		SendBuffer = new ArrayList<TimeStampedMessage>();
		Group gr = new Group();
		gr.setName("group");
		for (Node n : this.nodes) {
			gr.addMember(n.getName());
		}
		gr.setGroupSeqNum(0);
		groups.add(gr);

		this.MulitcastReceiveBuffer = new LinkedHashMap<Group, ArrayList<MulticastAckCountHelper>>();
		for (Group g : groups)
			this.MulitcastReceiveBuffer.put(g,
					new ArrayList<MulticastAckCountHelper>());

		if (type == TimeStampType.VECTOR) {
			timeStamp.setLength(nodes.size());
		}
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			if (n.getName().compareTo(this.Name) == 0) {
				if (type == TimeStampType.VECTOR)
					timeStamp.setIndex(i);
				ListenerThread listener = new ListenerThread(n.getPort(), this);
				Thread t = new Thread(listener);
				t.start();
				break;
			}
		}
	}

	public void send(Message message) {
		message.setSequenceNum(SequenceNum++);
		if (!message.isGroupMsg())
			message.setSource(this.Name);

		TimeStampedMessage timeMessage = new TimeStampedMessage(message);
		timeStamp.increment();
		timeMessage.setTimeStamp(timeStamp);

		// Check if config file has changed
		CheckConfig();
		Node destNode = getDestNode(timeMessage);
		try {
			if (destNode == null)
				throw new Exception("Destination node not found");

			TimeStampedMessage n = null;
			for (Rule r : sendRules) {
				if (r.isMatch(timeMessage)) {
					switch (r.getAction()) {
					case DROP:
						return;
					case DELAY:
						SendBuffer.add(timeMessage);
						return;
					case DUPLICATE:
						n = new TimeStampedMessage(timeMessage);
						n.setDuplicate(true);
						break;
					default:
						break;
					}
					break;
				}
			}
			if (destNode.getSocket() == null) {
				Socket s = new Socket(destNode.getIp(), destNode.getPort());
				destNode.setSocket(s);
			}
			ObjectOutputStream oos = destNode.getObjectOutputStream();
			oos.writeObject(timeMessage);
			if (n != null) {
				oos.writeObject(n);
			}
			while (!SendBuffer.isEmpty()) {
				n = SendBuffer.remove(0);
				if (n.getDestination().compareTo(timeMessage.getDestination()) == 0) {
					oos.writeObject(n);
				} else {
					destNode = getDestNode(n);
					oos = destNode.getObjectOutputStream();
					oos.writeObject(n);
					timeMessage = n;
				}
			}
		} catch (Exception e) {
			Log.d(((PosInfo)timeMessage.getData()).player, "err");
			//e.printStackTrace();
		}
	}

	public TimeStampedMessage receive() {

		// Check if config file has changed

		CheckConfig();

		TimeStampedMessage m = MessageReceivedQueue.poll();

		while (m == null)
			m = MessageReceivedQueue.poll();

		for (Rule r : receiveRules) {
			if (r.isMatch(m)) {
				switch (r.getAction()) {
				case DROP:
					return receive();
				case DELAY:
					if (m.isNoRecvDelay())
						break;
					m.setNoRecvDelay(true);
					ReceiveBuffer.add(m);
					return receive();
				case DUPLICATE:
					TimeStampedMessage n = new TimeStampedMessage(m);
					n.setDuplicate(true);
					MessageReceivedQueue.add(n);
					break;
				default:
					break;
				}
				break;
			}
		}
		timeStamp.increment(m);
		while (!ReceiveBuffer.isEmpty())
			MessageReceivedQueue.add(ReceiveBuffer.remove(0));
		return m;
	}

	public void MulticastSend(Message m) throws Exception {
		m.setGroupMsg(true);
		m.setGroupName(m.getDestination());
		Group g = getDestGroup(m);
		if (g == null)
			throw new Exception("Group not found");

		// check if this node is a member of the multicast group
		if (g.isMemberOf(this.Name)) {
			MulitcastReceiveBuffer.get(g)
					.add(new MulticastAckCountHelper(m, 1));
		}

		m.setGroupSeqNum(g.getGroupSeqNum());
		g.setGroupSeqNum(g.getGroupSeqNum() + 1);

		for (String member : g.getMembers()) {
			if (member.compareTo(this.Name) == 0)
				continue;
			m.setDestination(member);
			m.setSource(this.Name);
			send(m);
		}
	}

	private void CheckConfig() {
		if (ConfigFile == null)
			return;
		File file = new File(ConfigFile);
		if (ModifiedTime < file.lastModified()) {
			ModifiedTime = file.lastModified();
			// do update
			parseConfigFile(file, false);
		}
	}

	private Node getDestNode(Message message) {
		for (Node n : nodes)
			if (n.getName().compareTo(message.getDestination()) == 0)
				return n;
		return null;
	}

	private Group getDestGroup(Message m) {
		for (Group g : groups)
			if (g.getName().compareTo(m.getDestination()) == 0)
				return g;
		return null;
	}

	public void sendAck(String dest, TimeStampedMessage m) {
		if (dest.compareTo(this.Name) == 0)
			return;
		Message m2 = new Message(dest, "ack", m);
		send(m2);
	}

	private void parseConfigFile(File file, boolean doProcessConfig) {
		Yaml yaml = new Yaml();
		try {
			InputStream input = new FileInputStream(file);
			LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) yaml
					.load(input);
			if (doProcessConfig) {
				processConfig((ArrayList<LinkedHashMap<String, Object>>) data
						.get("configuration"));
				processGroups((ArrayList<LinkedHashMap<String, Object>>) data
						.get("groups"));
			}
			processDocument(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void processGroups(ArrayList<LinkedHashMap<String, Object>> groups) {
		ArrayList<LinkedHashMap<String, Object>> tmpMembers;

		for (LinkedHashMap<String, Object> group : groups) {
			Group g = new Group();
			g.setName(group.get("name").toString());
			tmpMembers = (ArrayList<LinkedHashMap<String, Object>>) group
					.get("members");
			for (Object member : tmpMembers.toArray()) {
				g.addMember(member.toString());
			}
			g.setGroupSeqNum(0);
			this.groups.add(g);
		}
		return;
	}

	private void processDocument(LinkedHashMap<String, Object> parsedData) {
		processSendRules((ArrayList<LinkedHashMap<String, Object>>) parsedData
				.get("sendRules"));
		processReceiveRules((ArrayList<LinkedHashMap<String, Object>>) parsedData
				.get("receiveRules"));
	}

	private void processReceiveRules(
			ArrayList<LinkedHashMap<String, Object>> recvRules) {
		for (LinkedHashMap<String, Object> ruleSet : recvRules) {
			Rule rule;
			try {
				rule = new Rule(ruleSet.get("action").toString());

				if (ruleSet.get("src") != null)
					rule.setSrc(ruleSet.get("src").toString());
				if (ruleSet.get("dest") != null)
					rule.setDst(ruleSet.get("dest").toString());
				if (ruleSet.get("seqNum") != null)
					rule.setSeq_num(Integer.parseInt(ruleSet.get("seqNum")
							.toString()));
				if (ruleSet.get("kind") != null)
					rule.setKind(ruleSet.get("kind").toString());
				if (ruleSet.get("duplicate") != null)
					rule.setDuplicate(ruleSet.get("duplicate").toString()
							.compareTo("true") == 0);

				this.receiveRules.add(rule);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processSendRules(
			ArrayList<LinkedHashMap<String, Object>> sendRules) {

		for (LinkedHashMap<String, Object> ruleSet : sendRules) {
			Rule rule;
			try {
				rule = new Rule(ruleSet.get("action").toString());

				if (ruleSet.get("src") != null)
					rule.setSrc(ruleSet.get("src").toString());
				if (ruleSet.get("dest") != null)
					rule.setDst(ruleSet.get("dest").toString());
				if (ruleSet.get("seqNum") != null)
					rule.setSeq_num(Integer.parseInt(ruleSet.get("seqNum")
							.toString()));
				if (ruleSet.get("kind") != null)
					rule.setKind(ruleSet.get("kind").toString());
				if (ruleSet.get("duplicate") != null)
					rule.setDuplicate(ruleSet.get("duplicate").toString()
							.compareTo("true") == 0);
				this.sendRules.add(rule);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void processConfig(ArrayList<LinkedHashMap<String, Object>> nodes) {
		for (LinkedHashMap<String, Object> newNode : nodes) {
			Node node = new Node();
			node.setName(newNode.get("name").toString());
			node.setIp(newNode.get("ip").toString());
			node.setPort(Integer.parseInt(newNode.get("port").toString()));
			this.nodes.add(node);
		}
	}

	public TimeStamp generateTimeStamp() {
		timeStamp.increment();
		return timeStamp;
	}

	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void cleanup() {
		for (Node n : nodes) {
			if (n.getName().compareTo(this.Name) == 0)
				continue;
			try {
				if (n != null)
					if (n.getObjectOutputStream() != null)
						n.getObjectOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public ArrayList<Node> get_node_list(){
		return this.nodes;
	}
}
