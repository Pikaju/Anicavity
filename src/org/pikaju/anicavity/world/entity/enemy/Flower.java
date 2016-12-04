package org.pikaju.anicavity.world.entity.enemy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.item.Item;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.weapon.projectiles.ProjectileLaser;

public class Flower extends Enemy {

	public static Sprite sprite = new Sprite("/mobs/flower.png", 2, 2);
	private byte anim = 0;
	private int timer = 0;
	
	public Flower() {
		width = 1;
		height = 1;
		isStatic = true;
		anim = (byte) MathHelper.getRandom().nextInt(4);
		setMaxHealth(50.0f);
		setHealth(getMaxHealth());
	}
	
	public void update() {
		super.update();
		if(isLocal()) {
			target = getNearestPlayer();
			if(target == null) return;
			float distanceToNearestPlayer = distanceToNearestPlayer();
			if(distanceToNearestPlayer > DESPAWN_RANGE) {
				remove();
				sendUpdatePacket();
			}
			if(distanceToNearestPlayer > UPDATE_RANGE) return;
			handleDamage();
			tick();
			if(moved) sendUpdatePacket();
			timer++;
			if(timer > 60) {
				if(timer % 10 == 0) {
					ProjectileLaser laser = new ProjectileLaser();
					laser.owner = this;
					laser.x = x + width / 2;
					laser.y = y + height / 2;
					laser.rotation = (float) Math.toDegrees(Math.atan2((y + height / 2) - (target.y + target.height / 2), (x + width / 2) - (target.x + target.width / 2))) + 180;
					world.addNewEntity(laser);
				}
				if(timer > 90) timer = 0;
			}
		} else {
			handleUpdatePackets();
		}
	}
	
	public void render(Bitmap screen, int scrollX, int scrollY) {
		float[] light = world.getLightAt((int) x, (int) y);
		float damageTimerFlash = getInvincibilityTimer() % 6 > 3 ? 4.0f : 1.0f;
		for(int i = 0; i < light.length; i++) light[i] *= damageTimerFlash;
		screen.draw(sprite.getBitmap(anim), x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY, width * Tile.SIZE, height * Tile.SIZE, 0, light);
		renderHealthBar(screen, scrollX, scrollY, 0xff0000, world.getAvarageLightAt((int) x, (int) y));
	}

	public void writeData(DataOutput out) {
		super.writeData(out);
		try {
			out.writeByte(anim);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		try {
			anim = in.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void damage(Entity source, float amount, float xDir, float yDir) {
		super.damage(source, amount, xDir, yDir);
		if(isLocal()) sendUpdatePacket();
	}
	
	public boolean isDamagable() {
		return true;
	}
	
	public int getInvincibilityTime() {
		return 30;
	}
	
	public void onKill() {
		super.onKill();
		world.dropItem(Item.RUBY, 2, x, y);
	}
}
