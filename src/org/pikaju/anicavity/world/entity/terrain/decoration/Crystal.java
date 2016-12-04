package org.pikaju.anicavity.world.entity.terrain.decoration;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Mob;
import org.pikaju.anicavity.world.light.Light;
import org.pikaju.anicavity.world.tile.Tile;

public class Crystal extends Mob {

	public static Sprite sprite = new Sprite("/mobs/crystal.png", 1, 1);
	
	private int timer = 0;
	private Light light;
	
	public Crystal() {
		width = 1;
		height = 1;
	}
	
	public void update() {
		super.update();
		if(light == null) {
			light = new Light(x, y, 1, 1, 3, 5.0f);
			world.getLightEngine().addLight(light, this);
		}
		if(distanceToNearestPlayer() < UPDATE_RANGE) {
			timer += 20;
			if(timer > 360) timer = 0;
			light.setRange(5.0f + MathHelper.sin(timer) * 0.5f);
		}
		if(!isLocal() && distanceToNearestPlayer() > DESPAWN_RANGE) {
			remove();
		}
	}
	
	public void render(Bitmap screen, int scrollX, int scrollY) {
		screen.draw(sprite.getBitmap(0), x * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY, width * Tile.SIZE, height * Tile.SIZE, 0, world.getLightAt((int) x, (int) y));
	}
}
