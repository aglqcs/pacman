package Team22.DS.cmu.edu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoggerNode {
	public static void main(String[] args) throws Exception {
		ArrayList<TimeStampedMessage> buf = new ArrayList<TimeStampedMessage>();
		if (args.length != 3) {
			System.out.println("Usage of the MessagePasser test program:");
			System.out
					.println("Requires 3 arguments: config_file name  host name and TimeStampType (specified by -v for vector or -l for logical)");
			return;
		}
		TimeStampType t;
		if (args[2].compareTo("-v") == 0)
			t = TimeStampType.VECTOR;
		else if (args[2].compareTo("-l") == 0)
			t = TimeStampType.LOGICAL;
		else {
			System.out
					.println("Error: must specify time stamp type with -v or -l");
			return;
		}

		System.out.println("Setting up the MessagePasser.");
		MessagePasser tester = new MessagePasser(args[0], args[1], t);

		BufferedReader user_input = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Welcome " + args[1]);
		System.out.println("To send a message, hit 's'."
				+ " A prompt will then ask for the data that is to be sent.");
		System.out
				.println("To receieve a message, hit 'r'."
						+ " This will wait until a message has be received and print it out.");
		System.out.println("To view the nodes current timestamp press 'c'.");
		System.out.println("To create an arbitrary timestamp press 't'.");
		System.out.println("To dump the log of messages press 'd'.");

		TimeStampedMessage receive = null;
		Message send = null;
		String destination;
		String kind;
		String data;
		while (true) {
			System.out.println("'r', 's', 'c', 't' or 'd'");
			String user_action;
			try {
				user_action = user_input.readLine();
				if (user_action.equals("r")) {
					receive = tester.receive();
					System.out.println("Message Received:");
					System.out.println("Source: " + receive.getSource()
							+ " Destination: " + receive.getDestination()
							+ " Sequence number: " + receive.getSequenceNum()
							+ " Kind: " + receive.getKind() + " Duplicate: "
							+ receive.getDuplicate() + " Data: "
							+ receive.getData() + " TimeStamp: "
							+ receive.getTimeStamp().toString());
					if (buf.size() == 0) {
						buf.add(receive);
						continue;
					}
					int i;
					for (i = 0; i < buf.size(); i++) {
						if (receive.getTimeStamp().compareTo(
								buf.get(i).getTimeStamp()) < 0) {
							buf.add(i, receive);
							break;
						}
					}
					if (i == buf.size())
						buf.add(receive);
				} else if (user_action.equals("s")) {
					System.out.println("Who is the destination?");
					destination = user_input.readLine();
					System.out.println("What kind of message is it?");
					kind = user_input.readLine();
					System.out.println("What data should be in it?");
					data = user_input.readLine();
					send = new Message(destination, kind, data);
					tester.send(send);
					System.out.println("Message sent to: " + destination);
				} else if (user_action.equals("c")) {
					System.out.println(tester.getTimeStamp().toString());
				} else if (user_action.equals("t")) {
					System.out.println(tester.generateTimeStamp().toString());
				} else if (user_action.equals("d")) {
					dump(buf);
				} else {
					System.out
							.println("Usage: 'r' to receive a message. 's' to send a message. 'c' to view current timestamp. 't' to create a updated timestamp.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void dump(ArrayList<TimeStampedMessage> buf) {
		ArrayList<ArrayList<TimeStampedMessage>> concurrentLists = new ArrayList<ArrayList<TimeStampedMessage>>();
		ArrayList<TimeStampedMessage> concurrentMessages = new ArrayList<TimeStampedMessage>();
		if (buf.size() > 0)
			concurrentMessages.add(buf.get(0));
		for (int i = 1; i < buf.size(); i++) {
			TimeStampedMessage prev = buf.get(i - 1);
			if (prev.getTimeStamp().compareTo(buf.get(i).getTimeStamp()) == 0)
				concurrentMessages.add(buf.get(i));
			else {
				concurrentLists.add(concurrentMessages);
				concurrentMessages = new ArrayList<TimeStampedMessage>();
				concurrentMessages.add(buf.get(i));
			}
		}
		concurrentLists.add(concurrentMessages);

		printResults(concurrentLists);
	}

	private static void printResults(
			ArrayList<ArrayList<TimeStampedMessage>> concurrentLists) {
		System.out.println("Begin Dump of Logged Messages:");
		System.out
				.println("=====================================================================");
		for (ArrayList<TimeStampedMessage> list : concurrentLists) {
			if (list.size() == 1) {
				TimeStampedMessage t = list.get(0);
				System.out.println("Source: " + t.getSource() + ", TimeStamp: "
						+ t.getTimeStamp().toString() + ", SeqNum:"
						+ t.getSequenceNum());
			} else {
				System.out.println("The following " + list.size()
						+ " messages were concurrent.");
				for (TimeStampedMessage t : list) {
					System.out.println("Source: " + t.getSource()
							+ ", TimeStamp: " + t.getTimeStamp().toString()
							+ ", SeqNum:" + t.getSequenceNum());
				}
			}
			System.out
					.println("=====================================================================");
		}
		System.out.println("End Dump");
	}
}
