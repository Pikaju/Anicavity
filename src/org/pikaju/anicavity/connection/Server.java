package org.pikaju.anicavity.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.pikaju.anicavity.connection.packet.Packet;
import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.entity.Player;

public class Server extends Thread {

	private static Server i;
	
	private boolean running;
	
	private DatagramSocket socket;
	public int port;
	
	private World world;
	
	public static void main(String[] args) {
		i = new Server(Integer.parseInt(args[0]));
		i.start();
	}
	
	public static void open(final int port) {
		new Thread(new Runnable() {
			public void run() {
				main(new String[] { port + "" });
			}
		}, "Anicavity Server").start();
	}
	
	public static void close() {
		i.running = false;
	}
	
	public Server(int port) {
		this.port = port;
		System.out.println("Starting Server on port " + port + "...");
		try {
			socket = new DatagramSocket(port);
		} catch(BindException e1) {
			return;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		world = new World(System.currentTimeMillis());
		world.setServer(this);
		world.generate();
	}
	
	public synchronized void start() {
		running = true;
		super.start();
		loop();
	}
	
	private void loop() {
		long currentTime = 0;
		long lastTime = System.nanoTime();
		double ns = 1000000000.0 / 60.0;
		double delta = 0;
		
		while(running) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / ns;
			lastTime = currentTime;
			while(delta >= 1) {
				world.update();
				delta--;
			}
		}
	}

	public void run() {
		if(socket == null) {
			running = false;
		} else {
			try {
				socket.setSoTimeout(1000);
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
		}
		while(running) {
			try {
				byte[] data = new byte[1024];
				DatagramPacket p = new DatagramPacket(data, data.length);
				socket.receive(p);
				
				ByteArrayInputStream in = new ByteArrayInputStream(p.getData());
				DataInputStream dis = new DataInputStream(in);
				
				Packet packet = Packet.readPacket(world, new Connection(p), dis, null, this);
				packet.processPacket();
				
				dis.close();
			} catch(SocketTimeoutException e) {
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(socket != null) {
			socket.close();
		}
		System.out.println("Stopping Server...");
	}
	
	public void send(byte[] data, Connection connection) {
		DatagramPacket packet = new DatagramPacket(data, data.length, connection.getIp(), connection.getPort());
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToAll(byte[] data) {
		Player[] players = world.getPlayers();
		for(int i = 0; i < players.length; i++) {
			send(data, players[i].connection);
		}
	}
	
	public void send(Packet packet, Connection connection) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			packet.writeData(dos);
			dos.flush();
			dos.close();
			send(out.toByteArray(), connection);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToAll(Packet packet) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			packet.writeData(dos);
			dos.flush();
			dos.close();
			sendToAll(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
