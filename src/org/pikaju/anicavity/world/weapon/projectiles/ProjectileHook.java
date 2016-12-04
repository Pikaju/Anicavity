package org.pikaju.anicavity.world.weapon.projectiles;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.Mob;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.tile.Tile;

public class ProjectileHook extends Projectile {

	private Sprite sprite = new Sprite("/weapons/hook/hook.png", 1, 1);
	private float rotation;

	private boolean attached = false;
	
	public ProjectileHook() {
		width = 1.0f;
		height = 1.0f;
		timer = -1;
	}
	
	public void update() {
		if(owner != null && owner instanceof Player) {
			((Player) owner).hook = this;
		}
		if(owner == null) remove();
		if(owner != null && world.getEntityByID(owner.getId()) != owner) remove();
		if(!isAttached()) tick();
		if(onGround) {
			attached = true;
			setDX(0);
			setDY(0.1f);
		}
		rotation = (float) Math.toDegrees(Math.atan2(getDY(), getDX())) - 90;
		updateAttachment();
		super.update();
	}

	public void render(Bitmap screen, int scrollX, int scrollY) {
		renderRope(screen, scrollX, scrollY);
		screen.draw(sprite.getBitmap(0), x * Tile.SIZE + scrollX, (y + 0.5f) * Tile.SIZE + scrollY, width * Tile.SIZE, height * Tile.SIZE, 0, world.getLightAt((int) x, (int) y), rotation);
	}

	private void renderRope(Bitmap screen, int scrollX, int scrollY) {
		if(owner == null) return;
		float fx = x + width / 2;
		float fy = y + height / 2;
		float tx = ((Mob) owner).x + ((Mob) owner).width / 2;
		float ty = ((Mob) owner).y + ((Mob) owner).height / 2;
		float dx = tx - fx;
		float dy = ty - fy;
		int max = 64;
		for(int i = 0; i < max; i++) {
			screen.draw(0x505050, fx * Tile.SIZE + scrollX, fy * Tile.SIZE + scrollY, 3, 3, world.getLightAt((int) fx, (int) fy));
			fx += dx / (float) max;
			fy += dy / (float) max;
		}
	}
	
	public boolean isAttached() {
		return attached;
	}

	public void updateAttachment() {
		if(!isAttached()) {
			if(owner != null) {
				Mob owner = (Mob) this.owner;
				float dist = distanceTo(owner);
				if(dist > 5) {
					move((owner.x - x) * 0.02f, (owner.y - y) * 0.02f);
				}
			}
		}
	}
	
	public void onHit(Entity entity) {
		if(entity == null) {
			attached = true;
		}
	}
}
