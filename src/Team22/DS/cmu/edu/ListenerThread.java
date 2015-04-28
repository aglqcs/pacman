/* File: ListenerThread.java
 * 
 * Author: Ryan Cutler rcutler@andrew.cmu.edu
 * Author: Jeff Brandon jdbrando@andrew.cmu.edu
 * 
 * Date: 1-29-2015
 * 
 * Description: A thread class for binding and listening on a port
 * 	for new connections.
 * */
package Team22.DS.cmu.edu;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread implements Runnable {

	private int ListenPort;
	private MessagePasser Passer;

	public ListenerThread(int port, MessagePasser passer) {
		ListenPort = port;
		Passer = passer;
	}

	public void run() {

		ServerSocket listener = null;
		try {
			listener = new ServerSocket(ListenPort);
			
			while (true) {
				Socket socket = listener.accept();
				CommunicationThread ct = new CommunicationThread(socket, Passer);
				Thread t = new Thread(ct);
				t.start();
			}
		} catch (EOFException ex) {
			//ignore
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (listener != null)
				try {
					listener.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
