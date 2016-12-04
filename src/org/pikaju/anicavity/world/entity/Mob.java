package org.pikaju.anicavity.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.connection.packet.Packet4DamageMob;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.tile.Tile;

public class Mob extends Entity {

	public float x;
	public float y;
	public float width;
	public float height;
	
	protected boolean moved = false;
	
	private float health;
	private float maxHealth;
	private int invincibilityTimer = 0;

	public Mob() {
	}
	
	public void update() {
		super.update();
		handleSpawn();
		moved = false;
	}

	public void writeData(DataOutput out) {
		super.writeData(out);
		try {
			out.writeFloat(x);
			out.writeFloat(y);
			out.writeFloat(health);
			out.writeFloat(maxHealth);
			out.writeInt(invincibilityTimer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		try {
			x = in.readFloat();
			y = in.readFloat();
			health = in.readFloat();
			maxHealth = in.readFloat();
			invincibilityTimer = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean move(float dx, float dy) {
		float movement = 0.0125f;
		boolean intersects = false;
		float lx = x;
		float ly = y;
		for(float xx = 0; xx < Math.abs(dx); xx += movement) {
			x += dx < 0 ? -movement : movement;
			if(collision()) {
				x -= dx < 0 ? -movement : movement;
				intersects = true;
				break;
			}
		}
		for(float yy = 0; yy < Math.abs(dy); yy += movement) {
			y += dy < 0 ? -movement : movement;
			if(collision()) {
				y -= dy < 0 ? -movement : movement;
				intersects = true;
				break;
			}
		}
		if(lx != x || ly != y) moved = true;
		return intersects;
	}
	
	public boolean collision() {
		float xa = 1.0f;
		float ya = 1.0f;
		if(width < 1) xa = width - 0.01f;
		if(height < 1) ya = height - 0.01f;
		for(float xx = x; xx <= x + width; xx += xa) {
			for(float yy = y; yy <= y + height; yy += ya) {
				if(world.getTileData((int) Math.floor(xx), (int) Math.floor(yy)).isSolid()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public float distanceTo(Mob mob) {
		if(mob == null) return Float.MAX_VALUE;
		float xd = (mob.x + mob.width / 2.0f) - (x + width / 2.0f);
		float yd = (mob.y + mob.height / 2.0f) - (y + height / 2.0f);
		return MathHelper.sqrt(xd * xd + yd * yd);
	}
	
	public void setMaxHealth(float maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	public float getMaxHealth() {
		return maxHealth;
	}
	
	public void setHealth(float health) {
		this.health = health;
	}
	
	public float getHealth() {
		return health;
	}
	
	public boolean isDamagable() {
		return false;
	}
	
	public void damage(Entity source, float amount, float xDir, float yDir) {
		if(invincibilityTimer > 0) return;
		
		if(!isLocal()) {
			Packet4DamageMob damageMob = new Packet4DamageMob();
			damageMob.setAmount(amount);
			damageMob.setId(id);
			damageMob.setDirection(xDir, yDir);
			if(source != null) damageMob.setSourceId(source.getId());
			world.registerPacket(damageMob);
			damageMob.send();
			return;
		}
		
		health -= amount;
		if(amount > 0) {
			invincibilityTimer = getInvincibilityTime();
			onDamage(amount);
			if(health <= 0) onKill();
		} else {
			onHeal(amount);
		}
		if(health > getMaxHealth()) health = getMaxHealth();
	}
	
	public void onDamage(float amount) {
		
	}
	
	public void onHeal(float amount) {
		
	}
	
	public void onKill() {
		remove();
	}
	
	public void handleDamage() {
		if(!isDamagable()) return;
		if(invincibilityTimer > 0) invincibilityTimer--;
		if(health < maxHealth) health += 0.02f;
	}
	
	public void renderHealthBar(Bitmap screen, int scrollX, int scrollY, int color, float[] light) {
		if(!isDamagable()) return;
		if(health >= maxHealth) return;
		screen.draw(0xffffff, x * Tile.SIZE + scrollX - 1, y * Tile.SIZE + scrollY - 3, width * Tile.SIZE + 2, 3, light);
		screen.draw(color, x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY - 2, width * Tile.SIZE * (health / maxHealth), 1, light);
	}
	
	public void renderHealthBar(Bitmap screen, int scrollX, int scrollY, int color) {
		if(!isDamagable()) return;
		if(health >= maxHealth) return;
		screen.draw(0xffffff, x * Tile.SIZE + scrollX - 1, y * Tile.SIZE + scrollY - 4, width * Tile.SIZE + 2, 3);
		screen.draw(color, x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY - 3, width * Tile.SIZE * (health / maxHealth), 1);
	}
	
	public void renderHealthBar(Bitmap screen, int scrollX, int scrollY, int color, float avarageLight) {
		renderHealthBar(screen, scrollX, scrollY, color, new float[] { avarageLight, avarageLight, avarageLight });
	}
	
	public boolean intersects(float x, float y, float width, float height) {
		return x + width >= this.x && y + height >= this.y && this.x + this.width > x && this.y + this.height > y;
	}
	
	public boolean intersects(Mob mob) {
		return intersects(mob.x, mob.y, mob.width, mob.height);
	}
	
	public Player getNearestPlayer() {
		Player nearest = null;
		for(Player player : world.getPlayers()) {
			if(nearest == null) {
				nearest = player;
			} else {
				if(player.distanceTo(this) < nearest.distanceTo(this)) {
					nearest = player;
				}
			}
		}
		return nearest;
	}
	
	public float distanceToNearestPlayer() {
		return distanceTo(getNearestPlayer());
	}
	
	public int getInvincibilityTime() {
		return 10;
	}
	
	public int getInvincibilityTimer() {
		return invincibilityTimer;
	}
}
