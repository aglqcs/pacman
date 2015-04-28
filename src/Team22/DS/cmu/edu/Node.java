/* File: Node.java
 * 
 * Author: Ryan Cutler rcutler@andrew.cmu.edu
 * Author: Jeff Brandon jdbrando@andrew.cmu.edu
 * 
 * Date: 1-29-2015
 * 
 * Description: A Class for representing an entity in
 * 	our communication system.
 * */
package Team22.DS.cmu.edu;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Node {

	private String Name;
	private String ip;
	private int port;
	private Socket sock;
	private ObjectOutputStream ObjectOutputStream2;

	public Node() {
			setSocket(null);
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return sock;
	}

	public void setSocket(Socket sock) {
		this.sock = sock;
	}

	public void establishConn(Socket s) {
		this.setSocket(s);
		try {
			this.setObjectOutputStream(new ObjectOutputStream(s.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ObjectOutputStream getObjectOutputStream() {
		if(ObjectOutputStream2!=null) return ObjectOutputStream2;
		try {
			ObjectOutputStream2 = new ObjectOutputStream(getSocket().getOutputStream());
			return ObjectOutputStream2;
		} catch (IOException e) {
			return null;
		}
	}

	public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
		ObjectOutputStream2 = objectOutputStream;
	}

}
