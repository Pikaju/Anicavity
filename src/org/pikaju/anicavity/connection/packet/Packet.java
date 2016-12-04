package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.HashMap;

import org.pikaju.anicavity.connection.Client;
import org.pikaju.anicavity.connection.Connection;
import org.pikaju.anicavity.connection.Server;
import org.pikaju.anicavity.world.World;

public abstract class Packet {

	private static HashMap<Integer, Class<? extends Packet>> packetMap;
	private static int cid = 0;
	
	static {
		packetMap = new HashMap<Integer, Class<? extends Packet>>();
		addPacket(Packet0Login.class);
		addPacket(Packet1Disconnect.class);
		addPacket(Packet2EntityUpdate.class);
		addPacket(Packet3NewEntity.class);
		addPacket(Packet4DamageMob.class);
		addPacket(Packet5RemoveEntity.class);
		addPacket(Packet6Ping.class);
		addPacket(Packet7PickupItem.class);
	}
	
	public Client client;
	public Server server;
	protected Connection connection;
	protected World world;
	
	public Packet() {
		
	}
	
	private static void addPacket(Class<? extends Packet> packetClass) {
		packetMap.put(cid, packetClass);
		cid++;
	}
	
	public abstract void readData(DataInput in);
	public abstract void writeData(DataOutput out);
	public abstract void processPacket();
	
	public void send(byte[] data) {
		if(isServer()) server.sendToAll(data);
		if(isClient()) client.send(data);
	}
	
	public void send() {
		if(isServer()) server.sendToAll(this);
		if(isClient()) client.send(this);
	}
	
	public boolean isClient() {
		return client != null;
	}
	
	public boolean isServer() {
		return server != null;
	}
	
	public static Packet readPacket(World world, Connection connection, DataInput in, Client client, Server server) {
		try {
			int packetID = in.readInt();
			Class<? extends Packet> packetClass = packetMap.get(packetID);
			Packet packet = packetClass.newInstance();
			packet.world = world;
			packet.connection = connection;
			packet.client = client;
			packet.server = server;
			packet.readData(in);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
