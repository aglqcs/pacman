/* File: CommunicationThread.java
 * 
 * Author: Ryan Cutler rcutler@andrew.cmu.edu
 * Author: Jeff Brandon jdbrando@andrew.cmu.edu
 * 
 * Date: 1-29-2015
 * 
 * Description: A thread class for continuous communication after initial 
 * 	connection has been established
 * */
package Team22.DS.cmu.edu;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.util.ArrayList;

public class CommunicationThread implements Runnable {

	Socket commSocket;
	private MessagePasser Passer;

	public CommunicationThread(Socket s, MessagePasser passer) {
		commSocket = s;
		Passer = passer;
	}

	public void run() {

		try {
			ObjectInputStream ois = new ObjectInputStream(
					commSocket.getInputStream());
			TimeStampedMessage m;

			while (true) {
				try {

					m = (TimeStampedMessage) ois.readObject();

					if (m != null) {
						if (m.getData().getClass() == TimeStampedMessage.class) {
							// we have an ack
							System.out.println("ack received!!");
							TimeStampedMessage tmpMsg = (TimeStampedMessage) m
									.getData();
							UpdateMulticastBuffer(tmpMsg);
							continue;
						}
						if (m.isGroupMsg()) {
							UpdateMulticastBuffer(m);
						} else {
							Passer.MessageReceivedQueue.add(m);
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (StreamCorruptedException ex) {
			// ignore
		} catch (EOFException exe) {
			// ignore
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void CheckMulticastBuffer(Group g, TimeStampedMessage m) {
		ArrayList<MulticastAckCountHelper> s;
		int i=0;
		s = Passer.MulitcastReceiveBuffer.get(g);
		if (s.size() > 0) {
			i = s.get(0).getCount();
			if (g.getMembers().size() == i) {
				Passer.MulitcastReceiveBuffer.get(g).remove(0);
				Passer.MessageReceivedQueue.add(m);
				System.out
						.println("\nAlert: Group Message ready to receive.\n");
				if (Passer.MulitcastReceiveBuffer.get(g).size() == 0)
					return;
				TimeStampedMessage tmp = (TimeStampedMessage) Passer.MulitcastReceiveBuffer
						.get(g).get(0).getMessage();
				CheckMulticastBuffer(g, tmp);
			}
		}
	}

	private void UpdateMulticastBuffer(TimeStampedMessage m) {
		Group keyGroup = null;
		ArrayList<MulticastAckCountHelper> groupMessageAckCountHelper;

		for (Group g : Passer.MulitcastReceiveBuffer.keySet()) {
			if (g.getName().compareTo(m.getGroupName()) == 0) {
				keyGroup = g;
				break;
			}
		}
		// use group to get map to groupSequenceNumber to
		// AckCount map
		groupMessageAckCountHelper = Passer.MulitcastReceiveBuffer
				.get(keyGroup);

		MulticastAckCountHelper tmpHelper = null;
		boolean found = false;
		for (MulticastAckCountHelper mach : groupMessageAckCountHelper) {
			if (m.getGroupSeqNum() == mach.getMessage().getGroupSeqNum()) {
				found = true;
				tmpHelper = mach;
				break;
			}
		}
		if (found) {
			tmpHelper.setCount(tmpHelper.getCount() + 1);
		} else {
			if (keyGroup.isMemberOf(m.getSource()))
				groupMessageAckCountHelper
						.add(new MulticastAckCountHelper(m, 2));
			else
				groupMessageAckCountHelper
						.add(new MulticastAckCountHelper(m, 1));
			// send ack
			for (String member : keyGroup.getMembers()) {
				Passer.sendAck(member, m);
			}
		}

		CheckMulticastBuffer(keyGroup, m);
	}

}
