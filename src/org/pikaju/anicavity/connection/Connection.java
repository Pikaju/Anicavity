package org.pikaju.anicavity.connection;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Connection {

	private InetAddress ip;
	private int port;
	
	public Connection(String ip, int port) {
		try {
			this.ip = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.port = port;
	}
	
	public Connection(DatagramPacket packet) {
		this(packet.getAddress(), packet.getPort());
	}
	
	public Connection(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public String getString() {
		return ip.getHostAddress() + ":" + port;
	}
	
	public int hashCode() {
		return ip.hashCode() | (port << 8);
	}
	
	public String toString() {
		return "Connection[ip = " + ip.getHostAddress() + ", port = " + port + "]";
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Connection)) return false;
		Connection c = (Connection) obj;
		return c.ip.equals(ip) && c.port == port;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
