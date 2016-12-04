package org.pikaju.anicavity.world.weapon.projectiles;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.world.tile.Tile;

public class ProjectileGrenade extends Projectile {

	public static Sprite sprite = new Sprite("/items/grenade.png", 1, 1);
	
	public ProjectileGrenade() {
		width = 0.5f;
		height = 0.5f;
		bouncyness = 0.8f;
	}
	
	public void update() {
		super.update();
		tick();
		if(onGround) setDX(getDX() * 0.95f);
	}
	
	public void render(Bitmap screen, int scrollX, int scrollY) {
		float[] light = world.getLightAt((int) x, (int) y);
		screen.draw(sprite.getBitmap(0), x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY, width * Tile.SIZE, height * Tile.SIZE, 0, light);
	}
}
