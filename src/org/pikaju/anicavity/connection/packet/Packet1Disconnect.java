package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Player;

public class Packet1Disconnect extends Packet {

	private String username;
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void readData(DataInput in) {
		try {
			username = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(1);
			out.writeUTF(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		if(isServer()) {
			System.out.println(username + " disconnected");
			send();
		}
		Player player = world.getPlayer(username);
		if(player != null) world.removeEntity(player.getId());
	}
}
