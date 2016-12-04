package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.Player;

public class Packet0Login extends Packet implements Runnable {

	private String username;
	private float x;
	private float y;
	private long id = -1;
	private long seed = -1;
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void readData(DataInput in) {
		try {
			username = in.readUTF();
			x = in.readFloat();
			y = in.readFloat();
			id = in.readLong();
			seed = in.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(0);
			out.writeUTF(username);
			out.writeFloat(x);
			out.writeFloat(y);
			out.writeLong(id);
			out.writeLong(seed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		if(isServer()) {
			System.out.println(username + " joined");
			Player player = new Player(username, -1, -1);
			world.getSpawnPoint().resetMob(player);
			player.connection = connection;
			world.addEntity(player);
			this.id = player.getId();
			this.x = player.x;
			this.y = player.y;
			this.username = player.username;
			this.seed = world.getSeed();
			send();
			new Thread(this, "Login").start();
		} else {
			Player player = new Player(username, x, y);
			world.addEntity(player, id);
			if(player.isLocal()) {
				world.setSeed(seed);
				world.generate();
			}
		}
	}

	public void run() {
		try {
			for(Player p : world.getPlayers()) {
				if(p.username.equals(username)) continue;
				Packet0Login loginPacket = new Packet0Login();
				loginPacket.id = p.getId();
				loginPacket.x = p.x;
				loginPacket.y = p.y;
				loginPacket.username = p.username;
				server.send(loginPacket, connection);
				Thread.sleep(100);
			}
			for(Entity e : world.getEntities()) {
				if(e instanceof Player) continue;
				e.setSendWhenPlayerNear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
