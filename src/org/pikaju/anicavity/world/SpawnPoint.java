package org.pikaju.anicavity.world;

import org.pikaju.anicavity.world.entity.Mob;

public class SpawnPoint {

	private float x;
	private float y;
	
	public SpawnPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void resetMob(Mob mob) {
		mob.x = x;
		mob.y = y - mob.height - 4;
	}
}
