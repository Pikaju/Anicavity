package org.pikaju.anicavity.world.entity.enemy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.item.Item;
import org.pikaju.anicavity.world.tile.Tile;

public class Slime extends Enemy {
	
	public static Sprite sprite = new Sprite("/mobs/slime.png", 2, 2);
	private byte anim;
	public int color = 0x5050ff;
	
	public Slime() {
		width = 1;
		height = 1;
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
			handleMovement();
			handleDamage();
			float damage = 10.0f;
			if(color == 0xff5050) damage = 20.0f;
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
		for(int i = 0; i < light.length; i++) {
			light[i] *= (((color >> (16 - i * 8)) & 0xff) / 255.0f);
			light[i] *= damageTimerFlash;
		}
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
			onGroundTimer++;
			if(onGroundTimer >= 30) {
				setDY(getDY() - 0.4f);
				if(color == 0xff5050) setDY(getDY() * 1.5f);
				if(target != null) {
					float speed = (float) (0.2f + (MathHelper.getRandom().nextFloat() * 0.2f - 0.1f));
					if(color == 0xff5050) speed *= 1.5f;
					if(MathHelper.getRandom().nextFloat() > 0.8f) speed = -speed * 0.7f;
					setDX(target.x + target.width / 2 < x + width / 2 ? -speed : speed);
				}
			}
			anim = (byte) (onGroundTimer / 10);
		}
	}
	
	public void writeData(DataOutput out) {
		super.writeData(out);
		try {
			out.writeByte(anim);
			out.writeInt(color);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		try {
			anim = in.readByte();
			color = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isDamagable() {
		return true;
	}
	
	public void onKill() {
		super.onKill();
		world.dropItem(Item.RUBY, 1, x, y);
	}
}
