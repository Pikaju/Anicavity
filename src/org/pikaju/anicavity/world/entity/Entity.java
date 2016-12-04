package org.pikaju.anicavity.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import org.pikaju.anicavity.connection.packet.Packet2EntityUpdate;
import org.pikaju.anicavity.connection.packet.Packet3NewEntity;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.world.World;

public class Entity {

	public static final float UPDATE_RANGE = 30.0f;
	public static final float DESPAWN_RANGE = 20.0f + UPDATE_RANGE;
	
	protected long id = -1;
	protected boolean removed;
	public World world;
	
	private boolean sendWhenPlayerNear;
	
	protected ArrayList<Packet2EntityUpdate> entityUpdates = new ArrayList<Packet2EntityUpdate>();
	
	public Entity() {
	}
	
	public void update() {
		
	}
	
	public void handleUpdatePackets() {
		if(entityUpdates.size() > 0) {
			Packet2EntityUpdate entityUpdate = entityUpdates.get(0);
			if(entityUpdate != null) {
				DataInput in = entityUpdate.getInStream();
				readData(in);
				entityUpdates.remove(0);
			}
			if(entityUpdates.size() > 100) entityUpdates.clear();
		}
	}
	
	public void handleSpawn() {
		if(sendWhenPlayerNear && this instanceof Mob && ((Mob) this).distanceToNearestPlayer() < UPDATE_RANGE) {
			Packet3NewEntity entityPacket = new Packet3NewEntity();
			world.registerPacket(entityPacket);
			entityPacket.setEntity(this);
			entityPacket.send();
			sendWhenPlayerNear = false;
		}
	}

	public void render(Bitmap screen, int scrollX, int scrollY) {
		
	}
	
	public void addEntityUpdate(Packet2EntityUpdate packet) {
		entityUpdates.add(packet);
	}
	
	public void sendUpdatePacket() {
		if(isLocal()) {
			Packet2EntityUpdate entityUpdate = new Packet2EntityUpdate();
			entityUpdate.setEntity(this);
			world.registerPacket(entityUpdate);
			entityUpdate.send();
		}
	}
	
	public void writeData(DataOutput out) {
		try {
			out.writeLong(id);
			out.writeBoolean(removed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		try {
			id = in.readLong();
			boolean r = in.readBoolean();
			if(r) remove();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isLocal() {
		if(world == null) return false;
		return world.username == null;
	}
	
	public String toString() {
		return "Entity[id = " + id + "]";
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void remove() {
		this.removed = true;
	}

	public World getWorld() {
		return world;
	}

	public void setLevel(World world) {
		this.world = world;
	}
	
	public void setSendWhenPlayerNear() {
		sendWhenPlayerNear = true;
	}
}
