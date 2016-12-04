package org.pikaju.anicavity.world.entity.enemy.boss;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.item.Item;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.weapon.projectiles.ProjectileLaser;

public class SlimeBoss extends Boss {

	public static Sprite sprite = new Sprite("/mobs/slime.png", 2, 2);
	private byte anim;
	
	public SlimeBoss() {
		width = 4;
		height = 4;
		setMaxHealth(500.0f);
		setHealth(getMaxHealth());
		isStatic = true;
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
			handleMovement();
			handleDamage();
			float damage = 50.0f;
			damagePlayerOnContact(damage);
			tick();
			sendUpdatePacket();
		} else {
			handleUpdatePackets();
		}
	}
	
	public void render(Bitmap screen, int scrollX, int scrollY) {
		float[] light = world.getLightAt((int) (x + width / 2), (int) (y + height / 2));
		float damageTimerFlash = getInvincibilityTimer() % 6 > 3 ? 4.0f : 1.0f;
		for(int i = 0; i < light.length; i++) light[i] *= damageTimerFlash;
		screen.draw(sprite.getBitmap(anim), x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY, width * Tile.SIZE, height * Tile.SIZE, 0, light);
		renderHealthBar(screen, scrollX, scrollY, 0xff0000, world.getAvarageLightAt((int) x, (int) y));
	}
	
	private int onGroundTimer = 0;
	public void handleMovement() {
		if(!onGround) {
			anim = 3;
			onGroundTimer = 0;
		} else {
			setDX(0);
			if(onGroundTimer == 0) {
				shootLasers();
			}
			onGroundTimer++;
			if(onGroundTimer >= 30) {
				setDY(getDY() - 0.6f);
				if(target != null) {
					float speed = (float) (0.2f + (MathHelper.getRandom().nextFloat() * 0.2f - 0.1f)) * 2.0f;
					if(MathHelper.getRandom().nextFloat() > 0.8f) speed = -speed * 0.7f;
					setDX(target.x + target.width / 2 < x + width / 2 ? -speed : speed);
				}
			}
			anim = (byte) (onGroundTimer / 10);
		}
	}
	
	private void shootLasers() {
		for(float angle = 0; angle < 360; angle += 45 / 2.0f) {
			ProjectileLaser laser = new ProjectileLaser();
			laser.owner = this;
			laser.x = x + width / 2;
			laser.y = y + height / 2;
			laser.rotation = angle;
			laser.speed = 0.25f;
			world.addNewEntity(laser);
		}
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
	
	public int getInvincibilityTime() {
		return 40;
	}
	
	public void onKill() {
		super.onKill();
		world.dropItem(Item.RUBY, 10, x + width / 2, y + height / 2);
	}
}
