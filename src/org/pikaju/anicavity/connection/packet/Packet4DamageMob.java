package org.pikaju.anicavity.connection.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.Mob;

public class Packet4DamageMob extends Packet {

	private float amount;
	private long id;
	private long sourceId;
	private float xDir;
	private float yDir;
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public void setDirection(float xDir, float yDir) {
		this.xDir = xDir;
		this.yDir = yDir;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setSourceId(long id) {
		this.sourceId = id;
	}

	public void readData(DataInput in) {
		try {
			id = in.readLong();
			sourceId = in.readLong();
			amount = in.readFloat();
			xDir = in.readFloat();
			yDir = in.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeData(DataOutput out) {
		try {
			out.writeInt(4);
			out.writeLong(id);
			out.writeLong(sourceId);
			out.writeFloat(amount);
			out.writeFloat(xDir);
			out.writeFloat(yDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPacket() {
		Entity e = world.getEntityByID(id);
		if(e != null && e instanceof Mob) {
			((Mob) e).damage(world.getEntityByID(sourceId), amount, xDir, yDir);
		}
		if(e == null) return;
		if(isServer() && e.isLocal()) {
			send();
		}
	}
}
