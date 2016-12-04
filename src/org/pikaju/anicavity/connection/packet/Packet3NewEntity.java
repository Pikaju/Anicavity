package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Entity;

public class Packet3NewEntity extends Packet {

	private Entity entity;
	
	public void setEntity(Entity e) {
		entity = e;
	}
	
	public void readData(DataInput in) {
		try {
			entity = (Entity) Class.forName(in.readUTF()).newInstance();
			entity.world = world;
			Packet2EntityUpdate entityUpdate = new Packet2EntityUpdate();
			entityUpdate.setEntity(entity);
			entityUpdate.readData(in);
			entity.handleUpdatePackets();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(3);
			out.writeUTF(entity.getClass().getName());
			Packet2EntityUpdate entityUpdate = new Packet2EntityUpdate();
			entityUpdate.setEntity(entity);
			entityUpdate.writeData(out, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		if(isServer()) {
			world.addEntity(entity);
			send();
		} else {
			world.addEntity(entity, entity.getId());
		}
	}
}
