package org.pikaju.anicavity.world.weapon.projectiles;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.Mob;
import org.pikaju.anicavity.world.entity.PhysicsMob;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.entity.enemy.Enemy;
import org.pikaju.anicavity.world.light.Light;

public class Projectile extends PhysicsMob {

	public static boolean PVP = true;

	public float rotation = 0.0f;
	public float speed = 0.5f;
	public float knockBack = 0.3f;
	
	protected int timer = 120;
	
	public Entity owner;
	
	protected Light light;
	
	private boolean wasShot = false;
	
	public Projectile() {
	}
	
	public void update() {
		super.update();
		
		if(!wasShot) onShoot();
		wasShot = true;

		timer--;
		if(timer == 0) remove();
		if(isLocal()) {
			for(Entity e : world.getEntities()) {
				if(owner == null) continue;
				if(e.getId() == owner.getId()) continue;
				if((e instanceof Enemy || (PVP && e instanceof Player)) && ((Mob) e).intersects(this)) {
					onHit(e);
					sendUpdatePacket();
				}
			}
			if(timer % 30 == 0) {
				sendUpdatePacket();
			}
		} else {
			handleUpdatePackets();
		}
		if(light != null) light.setPosition(x, y);
		if(collision()) {
			onHit(null);
		}
	}
	
	public void onHit(Entity entity) {
		remove();
	}
	
	public void onShoot() {
		
	}
	
	public void writeData(DataOutput out) {
		super.writeData(out);
		try {
			out.writeFloat(rotation);
			out.writeFloat(speed);
			long id = -1;
			if(owner != null) id = owner.getId();
			out.writeLong(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		try {
			rotation = in.readFloat();
			speed = in.readFloat();
			long ownerID = in.readLong();
			owner = world.getEntityByID(ownerID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}