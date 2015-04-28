/* File: Test.java
 * 
 * Author: Ryan Cutler rcutler@andrew.cmu.edu
 * Author: Jeff Brandon jdbrando@andrew.cmu.edu
 * 
 * Date: 1-29-2015
 * 
 * Description: A testing tool that reads input from standard input to
 * 	generate and receive messages.
 * */

package Team22.DS.cmu.edu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Usage of the MessagePasser test program:");
			System.out
					.println("Requires 3 arguments: config_file name  host name and TimeStampType (specified by -v for vector or -l for logical)");
			return;
		}
		TimeStampType t;
		if(args[2].compareTo("-v")==0)
			t = TimeStampType.VECTOR;
		else if(args[2].compareTo("-l")==0)
			t = TimeStampType.LOGICAL;
		else{
			System.out.println("Error: must specify time stamp type with -v or -l");
			return;
		}

		System.out.println("Setting up the MessagePasser.");
		MessagePasser tester = new MessagePasser(args[0], args[1], t);

		BufferedReader user_input = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Welcome " + args[1]);
		System.out.println("To send a message, hit 's'." +
						" A prompt will then ask for the data that is to be sent.");
		System.out.println("To receieve a message, hit 'r'." +
						" This will wait until a message has be received and print it out.");
		System.out.println("To send a multicast message, hit 'm'.");
		System.out.println("To view the nodes current timestamp press 'c'");
		System.out.println("To create an arbitrary timestamp press 't'");

		TimeStampedMessage receive = null;
		Message send = null;
		String destination;
		String kind;
		String data;
		while (true) {
			System.out.println("'r', 's', 'm', 'c' or 't'");
			String user_action;
			try {
				user_action = user_input.readLine();
				if (user_action.equals("r")) {
					receive = tester.receive();
					System.out.println("Message Received:");
					System.out.println("Source: " + receive.getSource() + " Destination: " + receive.getDestination() + " Sequence number: " + receive.getSequenceNum() + " Kind: " + receive.getKind() + " Duplicate: " + receive.getDuplicate() + " Data: " + receive.getData()+" TimeStamp: "+receive.getTimeStamp().toString());
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
				} else if(user_action.equals("m")){
					System.out.println("Which group to send to?");
					destination = user_input.readLine();
					System.out.println("What kind of message is it?");
					kind = user_input.readLine();
					System.out.println("What data should be in it?");
					data = user_input.readLine();
					send = new Message(destination, kind, data);
					tester.MulticastSend(send);
					System.out.println("Message sent to: " + destination);
				}else if(user_action.equals("c")){
					System.out.println(tester.getTimeStamp().toString());
				}else if(user_action.equals("t")){
					System.out.println(tester.generateTimeStamp().toString());
				}else {
					System.out
							.println("Usage: 'r' to receive a message. 's' to send a message. 'm' to send a multicast message, 'c' to view current timestamp. 't' to create a updated timestamp.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}