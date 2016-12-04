package org.pikaju.anicavity.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.PortUnreachableException;
import java.net.SocketException;

import org.pikaju.anicavity.connection.packet.Packet;
import org.pikaju.anicavity.connection.packet.Packet0Login;
import org.pikaju.anicavity.connection.packet.Packet1Disconnect;
import org.pikaju.anicavity.world.World;

public class Client extends Thread {

	private DatagramSocket socket;
	private boolean running;
	
	private Connection connection;
	
	private World world;
	
	public Client(World world) {
		this.world = world;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(Connection connection) {
		this.connection = connection;
		socket.connect(connection.getIp(), connection.getPort());
		Packet0Login loginPacket = new Packet0Login();
		loginPacket.setUsername(world.username);
		send(loginPacket);
	}
	
	public void disconnect() {
		Packet1Disconnect disconnectPacket = new Packet1Disconnect();
		disconnectPacket.setUsername(world.username);
		send(disconnectPacket);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		running = true;
		while(running) {
			try {
				byte[] data = new byte[1024];
				DatagramPacket p = new DatagramPacket(data, data.length);
				socket.receive(p);
				
				ByteArrayInputStream in = new ByteArrayInputStream(p.getData());
				DataInputStream dis = new DataInputStream(in);
				
				Packet packet = Packet.readPacket(world, connection, dis, this, null);
				packet.processPacket();
				
				dis.close();
			} catch (PortUnreachableException e) {
				System.out.println("Server closed");
				System.exit(0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void send(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, connection.getIp(), connection.getPort());
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Packet packet) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			packet.writeData(dos);
			dos.flush();
			dos.close();
			send(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
