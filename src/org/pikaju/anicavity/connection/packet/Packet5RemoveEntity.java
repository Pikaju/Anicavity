package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Entity;

public class Packet5RemoveEntity extends Packet {

	private long id;
	
	public void setEntity(Entity e) {
		id = e.getId();
	}
	
	public void readData(DataInput in) {
		try {
			id = in.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(5);
			out.writeLong(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		Entity e = world.getEntityByID(id);
		if(e == null) return;
		e.remove();
		if(isServer()) {
			send();
		}
	}
}
