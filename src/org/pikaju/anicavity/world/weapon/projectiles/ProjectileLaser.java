package org.pikaju.anicavity.world.weapon.projectiles;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.sound.Sound;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.Mob;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.light.Light;
import org.pikaju.anicavity.world.tile.Tile;

public class ProjectileLaser extends Projectile {

	public static Sound[] shoot = Sound.load("/sounds/laser/laser", 3);
	public static Sound[] hit = Sound.load("/sounds/laser/laserHit", 3);
	
	private boolean destroyBlocks = false;
	
	private static Bitmap bitmap = new Bitmap(1, 1, 0x00ff00);
	
	public ProjectileLaser() {
		width = 0.125f;
		height = 0.125f;
	}
	
	public void update() {
		if(light == null) {
			light = new Light(x, y, 0, 0.7f, 0, 6.0f);
			world.getLightEngine().addLight(light, this);
		}
		x += MathHelper.cos(rotation) * speed;
		y += MathHelper.sin(rotation) * speed;
		super.update();
	}
	
	public void render(Bitmap screen, int scrollX, int scrollY) {
		float fx = x;
		float fy = y;
		
		for(float i = 0; i < 0.5f; i += height) {
			screen.draw(bitmap, fx * Tile.SIZE + scrollX, fy * Tile.SIZE + scrollY, height * Tile.SIZE, height * Tile.SIZE, 0, 1, 1, 1);
			fx -= MathHelper.cos(rotation) * width;
			fy -= MathHelper.sin(rotation) * height;
		}
	}
	
	public void onHit(Entity entity) {
		if(world.isClient()) {
			Player player = world.getLocalPlayer();
			float distance = distanceTo(player);
			Sound.play(hit, 1.0f / (distance + 3.0f) * 4.0f);
		}
		if(entity == null && destroyBlocks) {
			float fx = x + MathHelper.cos(rotation) * (1.0f / Tile.SIZE * 4.0f);
			float fy = y + MathHelper.sin(rotation) * (1.0f / Tile.SIZE * 4.0f);
			world.getTileData((int) fx, (int) fy).setTile(Tile.AIR, 0);
		} else if(entity instanceof Mob) {
			((Mob) entity).damage(this, 20.0f, MathHelper.cos(rotation) * knockBack, MathHelper.sin(rotation) * knockBack);
		}
		remove();
	}
	
	public void onShoot() {
		if(world.isClient()) {
			Player player = world.getLocalPlayer();
			float distance = distanceTo(player);
			Sound.play(shoot, 1.0f / (distance + 3.0f) * 4.0f);
		}
	}
}