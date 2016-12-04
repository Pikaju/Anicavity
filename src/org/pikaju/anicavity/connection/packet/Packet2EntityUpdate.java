package org.pikaju.anicavity.connection.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Entity;

public class Packet2EntityUpdate extends Packet {

	private Entity entity;
	private byte[] data;
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public void readData(DataInput in) {
		try {
			long id = in.readLong();
			if(entity == null) entity = world.getEntityByID(id);
			if(entity != null) {
				entity.setId(id);
				data = new byte[1024];
				try {
					in.readFully(data);
				} catch(EOFException e) {}
				entity.addEntityUpdate(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(2);
			if(entity == null) return;
			out.writeLong(entity.getId());
			entity.writeData(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(DataOutput out, boolean id) {
		try {
			if(id) out.writeInt(2);
			if(entity == null) return;
			out.writeLong(entity.getId());
			entity.writeData(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		if(isServer()) {
			send();
		}
	}

	public DataInput getInStream() {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		return dis;
	}
}
