package org.pikaju.anicavity.world.weapon.components;

import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.weapon.WeaponComponent;
import org.pikaju.anicavity.world.weapon.WeaponContainer;
import org.pikaju.anicavity.world.weapon.projectiles.ProjectileLaser;

public class GreenLaserComponent extends WeaponComponent {
	
	public GreenLaserComponent(WeaponContainer container) {
		super(container);
	}

	public void update() {
		cooldown++;
	}
	
	public void shoot(Entity owner, float x, float y, float targetX, float targetY) {
		if(cooldown > 10) {
			ProjectileLaser laser = new ProjectileLaser();
			laser.x = x;
			laser.y = y;
			laser.rotation = (float) (Math.toDegrees(Math.atan2(targetY, targetX)) + (Math.random() - 0.5f) * 10.0f);
			laser.owner = owner;
			container.world.addNewEntity(laser);
			cooldown = 0;
		}
	}
}
