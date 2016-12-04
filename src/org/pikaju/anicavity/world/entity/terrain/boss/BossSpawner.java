package org.pikaju.anicavity.world.entity.terrain.boss;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Mob;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.entity.enemy.boss.SlimeBoss;
import org.pikaju.anicavity.world.tile.Tile;

public class BossSpawner extends Mob {

	public static Sprite sprite = new Sprite("/boss/bossspawner.png", 2, 2);
	
	public float anim;
	
	private int removeTime = 0;
	
	public BossSpawner() {
		width = 1;
		height = 1;
	}
	
	public void update() {
		super.update();
		anim += 0.1f;
		if(anim >= 4.0f) anim = 0;
		if(removeTime > 0) removeTime++;
		if(isLocal()) {
			Player player = getNearestPlayer();
			if(player != null) {
				if(intersects(player) && removeTime == 0) {
					removeTime++;
					sendUpdatePacket();
				}
			}
			if(removeTime > 180) {
				remove();
				spawnBoss();
				sendUpdatePacket();
			}
		} else {
			handleUpdatePackets();
		}
	}

	public void render(Bitmap screen, int scrollX, int scrollY) {
		screen.draw(sprite.getBitmap((int) anim), x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY, width * Tile.SIZE, height * Tile.SIZE, 0, removeTime == 0 ? 1 : 0, removeTime == 0 ? 0 : 1, 0);
	}
	
	private void spawnBoss() {
		int bossType = MathHelper.getRandom().nextInt(1);
		if(bossType == 0) {
			SlimeBoss boss = new SlimeBoss();
			boss.x = x + width / 2 - boss.width / 2;
			boss.y = y + height / 2 - boss.height / 2;
			world.addNewEntity(boss);
		}
	}
	
	public void writeData(DataOutput out) {
		super.writeData(out);
		try {
			out.writeInt(removeTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		try {
			removeTime = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
